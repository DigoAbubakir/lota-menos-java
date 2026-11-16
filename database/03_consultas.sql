-- =====================================================================
-- LotaMenos - Consultas de conferência (Oracle FIAP)
-- =====================================================================

-- 1) Quantidade de registros por tabela (esperado: 6 linhas, 18 estações, 26 linha_estacao,
--    8208 históricos, 6 usuários, 12 relatos, 6 consultas)
SELECT 'T_LM_LINHA' AS tabela, COUNT(*) AS total FROM T_LM_LINHA
UNION ALL SELECT 'T_LM_ESTACAO', COUNT(*) FROM T_LM_ESTACAO
UNION ALL SELECT 'T_LM_LINHA_ESTACAO', COUNT(*) FROM T_LM_LINHA_ESTACAO
UNION ALL SELECT 'T_LM_HISTORICO_LOTACAO', COUNT(*) FROM T_LM_HISTORICO_LOTACAO
UNION ALL SELECT 'T_LM_USUARIO', COUNT(*) FROM T_LM_USUARIO
UNION ALL SELECT 'T_LM_RELATO_LOTACAO', COUNT(*) FROM T_LM_RELATO_LOTACAO
UNION ALL SELECT 'T_LM_CONSULTA_VIAGEM', COUNT(*) FROM T_LM_CONSULTA_VIAGEM;

-- 2) Estações da Linha 3 em ordem, com as linhas de integração
SELECT le.NR_ORDEM, e.NM_ESTACAO, e.ST_INTEGRACAO,
       (SELECT LISTAGG(l2.NR_LINHA || '-' || l2.NM_LINHA, ', ') WITHIN GROUP (ORDER BY l2.NR_LINHA)
          FROM T_LM_LINHA_ESTACAO le2 JOIN T_LM_LINHA l2 ON l2.ID_LINHA = le2.ID_LINHA
         WHERE le2.ID_ESTACAO = e.ID_ESTACAO AND l2.NR_LINHA <> 3) AS integracoes
  FROM T_LM_LINHA_ESTACAO le
  JOIN T_LM_LINHA l ON l.ID_LINHA = le.ID_LINHA
  JOIN T_LM_ESTACAO e ON e.ID_ESTACAO = le.ID_ESTACAO
 WHERE l.NR_LINHA = 3
 ORDER BY le.NR_ORDEM;

-- 3) Top 5 estações mais cheias no pico da manhã (dia útil, 06:00-08:45, sentido Barra Funda)
SELECT * FROM (
    SELECT e.NM_ESTACAO, ROUND(AVG(h.NR_INDICE), 1) AS media_pico, MAX(h.NR_INDICE) AS maximo
      FROM T_LM_HISTORICO_LOTACAO h
      JOIN T_LM_ESTACAO e ON e.ID_ESTACAO = h.ID_ESTACAO
     WHERE h.DS_TIPO_DIA = 'UTIL' AND h.DS_SENTIDO = 'SENTIDO_BARRA_FUNDA'
       AND h.HR_FAIXA BETWEEN '06:00' AND '08:45'
     GROUP BY e.NM_ESTACAO
     ORDER BY media_pico DESC
) WHERE ROWNUM <= 5;

-- 4) Top 5 estações mais cheias no pico da tarde (dia útil, 17:00-19:45, sentido Itaquera)
SELECT * FROM (
    SELECT e.NM_ESTACAO, ROUND(AVG(h.NR_INDICE), 1) AS media_pico, MAX(h.NR_INDICE) AS maximo
      FROM T_LM_HISTORICO_LOTACAO h
      JOIN T_LM_ESTACAO e ON e.ID_ESTACAO = h.ID_ESTACAO
     WHERE h.DS_TIPO_DIA = 'UTIL' AND h.DS_SENTIDO = 'SENTIDO_ITAQUERA'
       AND h.HR_FAIXA BETWEEN '17:00' AND '19:45'
     GROUP BY e.NM_ESTACAO
     ORDER BY media_pico DESC
) WHERE ROWNUM <= 5;

-- 5) Curva do dia útil na estação Brás (sentido Barra Funda)
SELECT h.HR_FAIXA, h.NR_INDICE
  FROM T_LM_HISTORICO_LOTACAO h JOIN T_LM_ESTACAO e ON e.ID_ESTACAO = h.ID_ESTACAO
 WHERE e.NM_ESTACAO = 'Brás' AND h.DS_TIPO_DIA = 'UTIL' AND h.DS_SENTIDO = 'SENTIDO_BARRA_FUNDA'
 ORDER BY h.HR_FAIXA;

-- 6) Relatos dos últimos 30 minutos (horário de São Paulo)
SELECT r.ID_RELATO, u.NM_USUARIO, u.VL_REPUTACAO, e.NM_ESTACAO, r.DS_SENTIDO, r.DS_NIVEL, r.DT_RELATO
  FROM T_LM_RELATO_LOTACAO r
  JOIN T_LM_USUARIO u ON u.ID_USUARIO = r.ID_USUARIO
  JOIN T_LM_ESTACAO e ON e.ID_ESTACAO = r.ID_ESTACAO
 WHERE r.DT_RELATO >= CAST(SYSTIMESTAMP AT TIME ZONE 'America/Sao_Paulo' AS TIMESTAMP) - INTERVAL '30' MINUTE
 ORDER BY r.DT_RELATO DESC;

-- 7) Indicador de impacto (o mesmo que GET /impacto)
SELECT COUNT(*) AS total_consultas,
       SUM(CASE WHEN ST_SUGESTAO_ACEITA = 1 THEN 1 ELSE 0 END) AS aceitas,
       ROUND(100 * SUM(CASE WHEN ST_SUGESTAO_ACEITA = 1 THEN 1 ELSE 0 END) / NULLIF(COUNT(*), 0), 1) AS percentual_aceitas,
       ROUND(AVG(CASE WHEN ST_SUGESTAO_ACEITA = 1 THEN NR_INDICE_DESEJADO - NR_INDICE_SUGERIDO END), 1) AS reducao_media_pontos
  FROM T_LM_CONSULTA_VIAGEM;
