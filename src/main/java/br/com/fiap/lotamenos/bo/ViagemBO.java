package br.com.fiap.lotamenos.bo;

import br.com.fiap.lotamenos.dao.ConsultaViagemDAO;
import br.com.fiap.lotamenos.dao.EstacaoDAO;
import br.com.fiap.lotamenos.dao.LinhaDAO;
import br.com.fiap.lotamenos.dao.LinhaEstacaoDAO;
import br.com.fiap.lotamenos.dao.UsuarioDAO;
import br.com.fiap.lotamenos.dto.AceiteRequest;
import br.com.fiap.lotamenos.dto.PlanejarViagemRequest;
import br.com.fiap.lotamenos.dto.ViagemResponse;
import br.com.fiap.lotamenos.exception.EstacaoForaDoRecorteException;
import br.com.fiap.lotamenos.exception.JanelaInvalidaException;
import br.com.fiap.lotamenos.exception.RecursoNaoEncontradoException;
import br.com.fiap.lotamenos.exception.RegraNegocioException;
import br.com.fiap.lotamenos.model.ConsultaViagem;
import br.com.fiap.lotamenos.model.Estacao;
import br.com.fiap.lotamenos.model.Linha;
import br.com.fiap.lotamenos.model.LinhaEstacao;
import br.com.fiap.lotamenos.model.NivelLotacao;
import br.com.fiap.lotamenos.model.Sentido;
import br.com.fiap.lotamenos.model.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ViagemBO {
    public static final int DURACAO_MAXIMA_JANELA_MINUTOS = 180;

    private final ConsultaViagemDAO consultaDAO;
    private final UsuarioDAO usuarioDAO;
    private final EstacaoDAO estacaoDAO;
    private final LinhaDAO linhaDAO;
    private final LinhaEstacaoDAO linhaEstacaoDAO;
    private final LotacaoBO lotacaoBO;
    private final Clock clock;

    public ViagemBO(ConsultaViagemDAO consultaDAO, UsuarioDAO usuarioDAO, EstacaoDAO estacaoDAO, LinhaDAO linhaDAO,
                    LinhaEstacaoDAO linhaEstacaoDAO, LotacaoBO lotacaoBO, Clock clock) {
        this.consultaDAO = consultaDAO;
        this.usuarioDAO = usuarioDAO;
        this.estacaoDAO = estacaoDAO;
        this.linhaDAO = linhaDAO;
        this.linhaEstacaoDAO = linhaEstacaoDAO;
        this.lotacaoBO = lotacaoBO;
        this.clock = clock;
    }

    record AvaliacaoHorario(LocalTime horario, int indice, Estacao estacaoCritica) {
    }

    @Transactional
    public ViagemResponse planejar(PlanejarViagemRequest req) {
        Usuario usuario = usuarioDAO.findById(req.usuarioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Usuário com id " + req.usuarioId() + " não encontrado."));
        Estacao origem = buscarEstacao(req.origemId());
        Estacao destino = buscarEstacao(req.destinoId());
        if (origem.getId().equals(destino.getId())) {
            throw new RegraNegocioException("A origem e o destino precisam ser estações diferentes.");
        }

        Linha linha3 = linhaDAO.findByNumero(LinhaBO.NUMERO_LINHA_RECORTE)
                .orElseThrow(() -> new RecursoNaoEncontradoException("A Linha 3-Vermelha não está cadastrada."));
        LinhaEstacao leOrigem = linhaEstacaoDAO.findByLinhaIdAndEstacaoId(linha3.getId(), origem.getId())
                .orElseThrow(() -> EstacaoForaDoRecorteException.daEstacao(origem.getNome()));
        LinhaEstacao leDestino = linhaEstacaoDAO.findByLinhaIdAndEstacaoId(linha3.getId(), destino.getId())
                .orElseThrow(() -> EstacaoForaDoRecorteException.daEstacao(destino.getNome()));

        Sentido sentido = definirSentido(leOrigem.getOrdem(), leDestino.getOrdem());

        LocalTime inicio = HorarioUtil.parse(req.janelaInicio());
        LocalTime fim = HorarioUtil.parse(req.janelaFim());
        LocalTime desejado = req.horarioDesejado() != null ? HorarioUtil.parse(req.horarioDesejado()) : inicio;
        validarJanela(inicio, fim, desejado);

        LocalDate data = req.data() != null ? req.data() : LocalDate.now(clock);
        List<Estacao> trecho = montarTrecho(linha3, leOrigem.getOrdem(), leDestino.getOrdem(), sentido);
        MapaLotacao mapa = lotacaoBO.carregarMapa(linha3, sentido, data);

        List<AvaliacaoHorario> avaliacoes = gerarHorariosCandidatos(inicio, fim, desejado).stream()
                .map(h -> avaliar(h, trecho, mapa))
                .toList();
        AvaliacaoHorario avaliacaoDesejada = avaliacoes.stream()
                .filter(a -> a.horario().equals(desejado)).findFirst().orElseThrow();

        boolean exigeAcessibilidade = usuario.getPerfil().exigeAcessibilidade();
        List<AvaliacaoHorario> elegiveis = avaliacoes;
        boolean alerta = false;
        if (exigeAcessibilidade) {
            List<AvaliacaoHorario> confortaveis = avaliacoes.stream()
                    .filter(a -> a.indice() <= NivelLotacao.MODERADO.getIndiceMaximo())
                    .toList();
            if (confortaveis.isEmpty()) {
                alerta = true;
            } else {
                elegiveis = confortaveis;
            }
        }
        AvaliacaoHorario melhor = escolherMelhor(elegiveis, desejado);

        ConsultaViagem consulta = new ConsultaViagem();
        consulta.setUsuario(usuario);
        consulta.setOrigem(origem);
        consulta.setDestino(destino);
        consulta.setSentido(sentido);
        consulta.setDataViagem(data);
        consulta.setHorarioDesejado(HorarioUtil.formatar(desejado));
        consulta.setHorarioSugerido(HorarioUtil.formatar(melhor.horario()));
        consulta.setIndiceDesejado(avaliacaoDesejada.indice());
        consulta.setIndiceSugerido(melhor.indice());
        consulta.setEstacaoCritica(melhor.estacaoCritica());
        consulta.setDataConsulta(LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS));
        return paraResponse(consultaDAO.save(consulta));
    }

    public List<ViagemResponse> listar() {
        return consultaDAO.listarTodas().stream().map(this::paraResponse).toList();
    }

    public ViagemResponse buscar(Long id) {
        return paraResponse(buscarEntidade(id));
    }

    @Transactional
    public ViagemResponse registrarAceite(Long id, AceiteRequest req) {
        ConsultaViagem consulta = buscarEntidade(id);
        consulta.setSugestaoAceita(req.aceita());
        return paraResponse(consultaDAO.save(consulta));
    }

    @Transactional
    public void excluir(Long id) {
        consultaDAO.delete(buscarEntidade(id));
    }

    static Sentido definirSentido(int ordemOrigem, int ordemDestino) {
        return ordemDestino < ordemOrigem ? Sentido.SENTIDO_BARRA_FUNDA : Sentido.SENTIDO_ITAQUERA;
    }

    static void validarJanela(LocalTime inicio, LocalTime fim, LocalTime desejado) {
        if (!fim.isAfter(inicio)) {
            throw new JanelaInvalidaException("O fim da janela (" + HorarioUtil.formatar(fim)
                    + ") deve ser depois do início (" + HorarioUtil.formatar(inicio) + ").");
        }
        int duracao = HorarioUtil.minutosDoDia(fim) - HorarioUtil.minutosDoDia(inicio);
        if (duracao > DURACAO_MAXIMA_JANELA_MINUTOS) {
            throw new JanelaInvalidaException("A janela de saída pode ter no máximo 3 horas (informado: "
                    + duracao + " minutos).");
        }
        if (desejado.isBefore(inicio) || desejado.isAfter(fim)) {
            throw new JanelaInvalidaException("O horário desejado (" + HorarioUtil.formatar(desejado)
                    + ") precisa estar dentro da janela " + HorarioUtil.formatar(inicio) + "–"
                    + HorarioUtil.formatar(fim) + ".");
        }
    }

    static List<LocalTime> gerarHorariosCandidatos(LocalTime inicio, LocalTime fim, LocalTime desejado) {
        List<LocalTime> candidatos = new ArrayList<>();
        for (int m = HorarioUtil.minutosDoDia(inicio); m <= HorarioUtil.minutosDoDia(fim); m += HorarioUtil.MINUTOS_FAIXA) {
            candidatos.add(HorarioUtil.deMinutos(m));
        }
        if (!candidatos.contains(desejado)) {
            candidatos.add(desejado);
        }
        candidatos.sort(Comparator.naturalOrder());
        return candidatos;
    }

    private List<Estacao> montarTrecho(Linha linha, int ordemOrigem, int ordemDestino, Sentido sentido) {
        List<LinhaEstacao> todas = linhaEstacaoDAO.listarPorLinhaEmOrdem(linha.getId());
        List<Estacao> trecho = new ArrayList<>();
        if (sentido == Sentido.SENTIDO_ITAQUERA) {
            for (LinhaEstacao le : todas) {
                if (le.getOrdem() >= ordemOrigem && le.getOrdem() < ordemDestino) {
                    trecho.add(le.getEstacao());
                }
            }
        } else {
            for (int i = todas.size() - 1; i >= 0; i--) {
                LinhaEstacao le = todas.get(i);
                if (le.getOrdem() <= ordemOrigem && le.getOrdem() > ordemDestino) {
                    trecho.add(le.getEstacao());
                }
            }
        }
        return trecho;
    }

    static AvaliacaoHorario avaliar(LocalTime horario, List<Estacao> trecho, MapaLotacao mapa) {
        int maior = -1;
        Estacao critica = null;
        for (Estacao estacao : trecho) {
            int indice = mapa.indice(estacao.getId(), horario);
            if (indice > maior) {
                maior = indice;
                critica = estacao;
            }
        }
        return new AvaliacaoHorario(horario, maior, critica);
    }

    static AvaliacaoHorario escolherMelhor(List<AvaliacaoHorario> opcoes, LocalTime desejado) {
        int minutosDesejado = HorarioUtil.minutosDoDia(desejado);
        return opcoes.stream()
                .min(Comparator.comparingInt(AvaliacaoHorario::indice)
                        .thenComparingInt(a -> Math.abs(HorarioUtil.minutosDoDia(a.horario()) - minutosDesejado))
                        .thenComparing(AvaliacaoHorario::horario))
                .orElseThrow();
    }

    static String montarDica(String horarioDesejado, String horarioSugerido, String estacaoCritica, boolean alerta) {
        String trecho = estacaoCritica != null ? " Trecho mais cheio: " + estacaoCritica + "." : "";
        String dica = horarioSugerido.equals(horarioDesejado)
                ? "Seu horário já é o melhor da janela." + trecho
                : "Saindo às " + horarioSugerido + " em vez de " + horarioDesejado + " você evita o pico." + trecho;
        if (alerta) {
            dica += " Atenção: nenhum horário da janela ficou com lotação moderada ou menor; "
                    + "se puder, amplie a janela ou prefira o vagão próximo ao assento preferencial.";
        }
        return dica;
    }

    private ViagemResponse paraResponse(ConsultaViagem c) {
        boolean alerta = c.getUsuario().getPerfil().exigeAcessibilidade()
                && c.getIndiceSugerido() > NivelLotacao.MODERADO.getIndiceMaximo();
        String critica = c.getEstacaoCritica() != null ? c.getEstacaoCritica().getNome() : null;
        return new ViagemResponse(
                c.getId(),
                c.getOrigem().getNome(),
                c.getDestino().getNome(),
                c.getSentido(),
                c.getHorarioDesejado(),
                c.getIndiceDesejado(),
                NivelLotacao.deIndice(c.getIndiceDesejado()),
                c.getHorarioSugerido(),
                c.getIndiceSugerido(),
                NivelLotacao.deIndice(c.getIndiceSugerido()),
                c.getIndiceDesejado() - c.getIndiceSugerido(),
                critica,
                alerta,
                montarDica(c.getHorarioDesejado(), c.getHorarioSugerido(), critica, alerta),
                c.getUsuario().getId(),
                c.getDataViagem(),
                c.getSugestaoAceita());
    }

    private Estacao buscarEstacao(Long id) {
        return estacaoDAO.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estação com id " + id + " não encontrada."));
    }

    private ConsultaViagem buscarEntidade(Long id) {
        return consultaDAO.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Consulta de viagem com id " + id + " não encontrada."));
    }
}
