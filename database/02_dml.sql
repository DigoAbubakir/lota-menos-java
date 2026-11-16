INSERT INTO T_LM_LINHA (NR_LINHA, NM_LINHA, DS_COR, DS_OPERADORA) VALUES (1,  'Azul',     '#0455A1', 'METRO');
INSERT INTO T_LM_LINHA (NR_LINHA, NM_LINHA, DS_COR, DS_OPERADORA) VALUES (3,  'Vermelha', '#EE372F', 'METRO');
INSERT INTO T_LM_LINHA (NR_LINHA, NM_LINHA, DS_COR, DS_OPERADORA) VALUES (7,  'Rubi',     '#CA016B', 'CPTM');
INSERT INTO T_LM_LINHA (NR_LINHA, NM_LINHA, DS_COR, DS_OPERADORA) VALUES (10, 'Turquesa', '#008B8B', 'CPTM');
INSERT INTO T_LM_LINHA (NR_LINHA, NM_LINHA, DS_COR, DS_OPERADORA) VALUES (11, 'Coral',    '#F04E23', 'CPTM');
INSERT INTO T_LM_LINHA (NR_LINHA, NM_LINHA, DS_COR, DS_OPERADORA) VALUES (12, 'Safira',   '#083E89', 'CPTM');

INSERT INTO T_LM_ESTACAO (NM_ESTACAO, ST_INTEGRACAO, ST_ATIVA) VALUES ('Palmeiras-Barra Funda', 1, 1);
INSERT INTO T_LM_ESTACAO (NM_ESTACAO, ST_INTEGRACAO, ST_ATIVA) VALUES ('Marechal Deodoro',      0, 1);
INSERT INTO T_LM_ESTACAO (NM_ESTACAO, ST_INTEGRACAO, ST_ATIVA) VALUES ('Santa Cecília',         0, 1);
INSERT INTO T_LM_ESTACAO (NM_ESTACAO, ST_INTEGRACAO, ST_ATIVA) VALUES ('República',             0, 1);
INSERT INTO T_LM_ESTACAO (NM_ESTACAO, ST_INTEGRACAO, ST_ATIVA) VALUES ('Anhangabaú',            0, 1);
INSERT INTO T_LM_ESTACAO (NM_ESTACAO, ST_INTEGRACAO, ST_ATIVA) VALUES ('Sé',                    1, 1);
INSERT INTO T_LM_ESTACAO (NM_ESTACAO, ST_INTEGRACAO, ST_ATIVA) VALUES ('Pedro II',              0, 1);
INSERT INTO T_LM_ESTACAO (NM_ESTACAO, ST_INTEGRACAO, ST_ATIVA) VALUES ('Brás',                  1, 1);
INSERT INTO T_LM_ESTACAO (NM_ESTACAO, ST_INTEGRACAO, ST_ATIVA) VALUES ('Bresser-Mooca',         0, 1);
INSERT INTO T_LM_ESTACAO (NM_ESTACAO, ST_INTEGRACAO, ST_ATIVA) VALUES ('Belém',                 0, 1);
INSERT INTO T_LM_ESTACAO (NM_ESTACAO, ST_INTEGRACAO, ST_ATIVA) VALUES ('Tatuapé',               1, 1);
INSERT INTO T_LM_ESTACAO (NM_ESTACAO, ST_INTEGRACAO, ST_ATIVA) VALUES ('Carrão',                0, 1);
INSERT INTO T_LM_ESTACAO (NM_ESTACAO, ST_INTEGRACAO, ST_ATIVA) VALUES ('Penha',                 0, 1);
INSERT INTO T_LM_ESTACAO (NM_ESTACAO, ST_INTEGRACAO, ST_ATIVA) VALUES ('Vila Matilde',          0, 1);
INSERT INTO T_LM_ESTACAO (NM_ESTACAO, ST_INTEGRACAO, ST_ATIVA) VALUES ('Guilhermina-Esperança', 0, 1);
INSERT INTO T_LM_ESTACAO (NM_ESTACAO, ST_INTEGRACAO, ST_ATIVA) VALUES ('Patriarca',             0, 1);
INSERT INTO T_LM_ESTACAO (NM_ESTACAO, ST_INTEGRACAO, ST_ATIVA) VALUES ('Artur Alvim',           0, 1);
INSERT INTO T_LM_ESTACAO (NM_ESTACAO, ST_INTEGRACAO, ST_ATIVA) VALUES ('Corinthians-Itaquera',  1, 1);

DECLARE
    TYPE t_nomes IS TABLE OF VARCHAR2(60);
    v_nomes t_nomes := t_nomes('Palmeiras-Barra Funda', 'Marechal Deodoro', 'Santa Cecília', 'República',
                               'Anhangabaú', 'Sé', 'Pedro II', 'Brás', 'Bresser-Mooca', 'Belém', 'Tatuapé',
                               'Carrão', 'Penha', 'Vila Matilde', 'Guilhermina-Esperança', 'Patriarca',
                               'Artur Alvim', 'Corinthians-Itaquera');
    v_linha NUMBER;
BEGIN
    SELECT ID_LINHA INTO v_linha FROM T_LM_LINHA WHERE NR_LINHA = 3;
    FOR i IN 1 .. v_nomes.COUNT LOOP
        INSERT INTO T_LM_LINHA_ESTACAO (ID_LINHA, ID_ESTACAO, NR_ORDEM)
        SELECT v_linha, ID_ESTACAO, i FROM T_LM_ESTACAO WHERE NM_ESTACAO = v_nomes(i);
    END LOOP;
END;
/

INSERT INTO T_LM_LINHA_ESTACAO (ID_LINHA, ID_ESTACAO, NR_ORDEM)
SELECT l.ID_LINHA, e.ID_ESTACAO, 1 FROM T_LM_LINHA l, T_LM_ESTACAO e WHERE l.NR_LINHA = 1  AND e.NM_ESTACAO = 'Sé';
INSERT INTO T_LM_LINHA_ESTACAO (ID_LINHA, ID_ESTACAO, NR_ORDEM)
SELECT l.ID_LINHA, e.ID_ESTACAO, 1 FROM T_LM_LINHA l, T_LM_ESTACAO e WHERE l.NR_LINHA = 7  AND e.NM_ESTACAO = 'Palmeiras-Barra Funda';
INSERT INTO T_LM_LINHA_ESTACAO (ID_LINHA, ID_ESTACAO, NR_ORDEM)
SELECT l.ID_LINHA, e.ID_ESTACAO, 1 FROM T_LM_LINHA l, T_LM_ESTACAO e WHERE l.NR_LINHA = 10 AND e.NM_ESTACAO = 'Brás';
INSERT INTO T_LM_LINHA_ESTACAO (ID_LINHA, ID_ESTACAO, NR_ORDEM)
SELECT l.ID_LINHA, e.ID_ESTACAO, 1 FROM T_LM_LINHA l, T_LM_ESTACAO e WHERE l.NR_LINHA = 11 AND e.NM_ESTACAO = 'Brás';
INSERT INTO T_LM_LINHA_ESTACAO (ID_LINHA, ID_ESTACAO, NR_ORDEM)
SELECT l.ID_LINHA, e.ID_ESTACAO, 2 FROM T_LM_LINHA l, T_LM_ESTACAO e WHERE l.NR_LINHA = 11 AND e.NM_ESTACAO = 'Tatuapé';
INSERT INTO T_LM_LINHA_ESTACAO (ID_LINHA, ID_ESTACAO, NR_ORDEM)
SELECT l.ID_LINHA, e.ID_ESTACAO, 3 FROM T_LM_LINHA l, T_LM_ESTACAO e WHERE l.NR_LINHA = 11 AND e.NM_ESTACAO = 'Corinthians-Itaquera';
INSERT INTO T_LM_LINHA_ESTACAO (ID_LINHA, ID_ESTACAO, NR_ORDEM)
SELECT l.ID_LINHA, e.ID_ESTACAO, 1 FROM T_LM_LINHA l, T_LM_ESTACAO e WHERE l.NR_LINHA = 12 AND e.NM_ESTACAO = 'Brás';
INSERT INTO T_LM_LINHA_ESTACAO (ID_LINHA, ID_ESTACAO, NR_ORDEM)
SELECT l.ID_LINHA, e.ID_ESTACAO, 2 FROM T_LM_LINHA l, T_LM_ESTACAO e WHERE l.NR_LINHA = 12 AND e.NM_ESTACAO = 'Tatuapé';

INSERT INTO T_LM_USUARIO (NM_USUARIO, DS_EMAIL, DS_PERFIL, VL_REPUTACAO, DT_CADASTRO)
VALUES ('Carlos Mendes', 'carlos.mendes@exemplo.com', 'TRABALHADOR', 1.20,
        CAST(SYSTIMESTAMP AT TIME ZONE '-03:00' AS TIMESTAMP) - INTERVAL '60' DAY);
INSERT INTO T_LM_USUARIO (NM_USUARIO, DS_EMAIL, DS_PERFIL, VL_REPUTACAO, DT_CADASTRO)
VALUES ('Dona Maria Aparecida', 'maria.aparecida@exemplo.com', 'IDOSO', 1.00,
        CAST(SYSTIMESTAMP AT TIME ZONE '-03:00' AS TIMESTAMP) - INTERVAL '45' DAY);
INSERT INTO T_LM_USUARIO (NM_USUARIO, DS_EMAIL, DS_PERFIL, VL_REPUTACAO, DT_CADASTRO)
VALUES ('Juliana Rocha', 'juliana.rocha@exemplo.com', 'MOBILIDADE_REDUZIDA', 1.10,
        CAST(SYSTIMESTAMP AT TIME ZONE '-03:00' AS TIMESTAMP) - INTERVAL '40' DAY);
INSERT INTO T_LM_USUARIO (NM_USUARIO, DS_EMAIL, DS_PERFIL, VL_REPUTACAO, DT_CADASTRO)
VALUES ('Pedro Lima', 'pedro.lima@exemplo.com', 'ESTUDANTE', 0.90,
        CAST(SYSTIMESTAMP AT TIME ZONE '-03:00' AS TIMESTAMP) - INTERVAL '30' DAY);
INSERT INTO T_LM_USUARIO (NM_USUARIO, DS_EMAIL, DS_PERFIL, VL_REPUTACAO, DT_CADASTRO)
VALUES ('Ana Souza', 'ana.souza@exemplo.com', 'TRABALHADOR', 1.30,
        CAST(SYSTIMESTAMP AT TIME ZONE '-03:00' AS TIMESTAMP) - INTERVAL '20' DAY);
INSERT INTO T_LM_USUARIO (NM_USUARIO, DS_EMAIL, DS_PERFIL, VL_REPUTACAO, DT_CADASTRO)
VALUES ('Roberto Alves', 'roberto.alves@exemplo.com', 'TRABALHADOR', 1.05,
        CAST(SYSTIMESTAMP AT TIME ZONE '-03:00' AS TIMESTAMP) - INTERVAL '10' DAY);

DECLARE
    TYPE t_int IS TABLE OF PLS_INTEGER INDEX BY PLS_INTEGER;
    TYPE t_txt IS TABLE OF VARCHAR2(20) INDEX BY PLS_INTEGER;
    v_peso_manha t_int;
    v_peso_tarde t_int;
    v_curva_manha t_int;
    v_curva_tarde t_int;
    v_sentido t_txt;
    v_tipo t_txt;
    v_fator t_int;
    v_linha  NUMBER;
    v_min    PLS_INTEGER;
    v_bonus  PLS_INTEGER;
    v_base   PLS_INTEGER;
    v_valor  PLS_INTEGER;
    v_ruido  PLS_INTEGER;
    v_indice PLS_INTEGER;
    v_faixa  VARCHAR2(5);

    PROCEDURE carregar(p_tab IN OUT t_int, p_inicio PLS_INTEGER, p_valores sys.odcinumberlist) IS
    BEGIN
        FOR i IN 1 .. p_valores.COUNT LOOP
            p_tab(p_inicio + i - 1) := p_valores(i);
        END LOOP;
    END;
BEGIN
    carregar(v_peso_manha, 1, sys.odcinumberlist(40, 45, 52, 62, 70, 94, 95, 100, 98, 97, 96, 94, 90, 86, 82, 78, 74, 70));
    carregar(v_peso_tarde, 1, sys.odcinumberlist(65, 70, 75, 85, 90, 97, 99, 100, 98, 97, 95, 85, 80, 75, 70, 66, 62, 20));
    carregar(v_curva_manha, 0, sys.odcinumberlist(30, 35, 40, 45, 50, 56, 62, 80, 96, 100, 100, 100, 97, 90, 80, 68));
    carregar(v_curva_tarde, 0, sys.odcinumberlist(70, 80, 92, 100, 100, 100, 97, 90, 82, 74, 66, 58));
    v_sentido(1) := 'SENTIDO_BARRA_FUNDA';
    v_sentido(2) := 'SENTIDO_ITAQUERA';
    v_tipo(1) := 'UTIL';    v_fator(1) := 100;
    v_tipo(2) := 'SABADO';  v_fator(2) := 55;
    v_tipo(3) := 'DOMINGO'; v_fator(3) := 40;

    SELECT ID_LINHA INTO v_linha FROM T_LM_LINHA WHERE NR_LINHA = 3;

    FOR le IN (SELECT ID_ESTACAO, NR_ORDEM FROM T_LM_LINHA_ESTACAO WHERE ID_LINHA = v_linha ORDER BY NR_ORDEM) LOOP
        v_bonus := GREATEST(0, 10 - ABS(le.NR_ORDEM - 7));
        FOR s IN 1 .. 2 LOOP
            FOR t IN 1 .. 3 LOOP
                FOR k IN 0 .. 75 LOOP
                    v_min := 300 + k * 15;
                    IF v_min < 540 THEN
                        IF s = 1 THEN
                            v_base := GREATEST(20, ROUND(96 * v_peso_manha(le.NR_ORDEM) * v_curva_manha(k) / 10000));
                        ELSIF v_min < 360 THEN
                            v_base := 20 + v_bonus;
                        ELSE
                            v_base := 40 + v_bonus;
                        END IF;
                    ELSIF v_min < 1020 THEN
                        v_base := 40 + v_bonus;
                    ELSIF v_min < 1200 THEN
                        IF s = 2 THEN
                            v_base := GREATEST(40 + v_bonus,
                                               ROUND(96 * v_peso_tarde(le.NR_ORDEM) * v_curva_tarde(k - 48) / 10000));
                        ELSE
                            v_base := 40 + v_bonus;
                        END IF;
                    ELSIF v_min < 1260 THEN
                        v_base := 30 + v_bonus;
                    ELSE
                        v_base := 19 + v_bonus;
                    END IF;

                    v_valor  := ROUND(v_base * v_fator(t) / 100);
                    v_ruido  := MOD(le.NR_ORDEM * 7 + k * 3, 9) - 4;
                    v_indice := LEAST(100, GREATEST(0, v_valor + v_ruido));
                    v_faixa  := LPAD(TRUNC(v_min / 60), 2, '0') || ':' || LPAD(MOD(v_min, 60), 2, '0');

                    INSERT INTO T_LM_HISTORICO_LOTACAO (ID_ESTACAO, ID_LINHA, DS_SENTIDO, DS_TIPO_DIA, HR_FAIXA, NR_INDICE)
                    VALUES (le.ID_ESTACAO, v_linha, v_sentido(s), v_tipo(t), v_faixa, v_indice);
                END LOOP;
            END LOOP;
        END LOOP;
    END LOOP;
END;
/

DECLARE
    v_agora TIMESTAMP := CAST(SYSTIMESTAMP AT TIME ZONE '-03:00' AS TIMESTAMP);

    PROCEDURE relato(p_email VARCHAR2, p_estacao VARCHAR2, p_sentido VARCHAR2, p_nivel VARCHAR2, p_min NUMBER) IS
    BEGIN
        INSERT INTO T_LM_RELATO_LOTACAO (ID_USUARIO, ID_ESTACAO, ID_LINHA, DS_SENTIDO, DS_NIVEL, DT_RELATO)
        SELECT u.ID_USUARIO, e.ID_ESTACAO, l.ID_LINHA, p_sentido, p_nivel, v_agora - NUMTODSINTERVAL(p_min, 'MINUTE')
          FROM T_LM_USUARIO u, T_LM_ESTACAO e, T_LM_LINHA l
         WHERE u.DS_EMAIL = p_email AND e.NM_ESTACAO = p_estacao AND l.NR_LINHA = 3;
    END;
BEGIN
    relato('carlos.mendes@exemplo.com',   'Artur Alvim',          'SENTIDO_BARRA_FUNDA', 'CHEIO',    3);
    relato('ana.souza@exemplo.com',       'Brás',                 'SENTIDO_BARRA_FUNDA', 'LOTADO',   5);
    relato('roberto.alves@exemplo.com',   'Tatuapé',              'SENTIDO_BARRA_FUNDA', 'LOTADO',   8);
    relato('pedro.lima@exemplo.com',      'Sé',                   'SENTIDO_ITAQUERA',    'MODERADO', 12);
    relato('maria.aparecida@exemplo.com', 'Penha',                'SENTIDO_BARRA_FUNDA', 'CHEIO',    15);
    relato('juliana.rocha@exemplo.com',   'Carrão',               'SENTIDO_BARRA_FUNDA', 'CHEIO',    18);
    relato('carlos.mendes@exemplo.com',   'Belém',                'SENTIDO_BARRA_FUNDA', 'LOTADO',   20);
    relato('ana.souza@exemplo.com',       'Bresser-Mooca',        'SENTIDO_ITAQUERA',    'MODERADO', 25);
    relato('roberto.alves@exemplo.com',   'República',            'SENTIDO_ITAQUERA',    'VAZIO',    28);
    relato('pedro.lima@exemplo.com',      'Corinthians-Itaquera', 'SENTIDO_BARRA_FUNDA', 'MODERADO', 40);
    relato('juliana.rocha@exemplo.com',   'Sé',                   'SENTIDO_BARRA_FUNDA', 'LOTADO',   60);
    relato('maria.aparecida@exemplo.com', 'Santa Cecília',        'SENTIDO_ITAQUERA',    'VAZIO',    120);
END;
/

DECLARE
    v_agora TIMESTAMP := CAST(SYSTIMESTAMP AT TIME ZONE '-03:00' AS TIMESTAMP);

    PROCEDURE consulta(p_email VARCHAR2, p_origem VARCHAR2, p_destino VARCHAR2, p_sentido VARCHAR2,
                       p_desejado VARCHAR2, p_sugerido VARCHAR2, p_ind_des NUMBER, p_ind_sug NUMBER,
                       p_critica VARCHAR2, p_aceita NUMBER, p_horas_atras NUMBER) IS
        v_quando TIMESTAMP := v_agora - NUMTODSINTERVAL(p_horas_atras, 'HOUR');
    BEGIN
        INSERT INTO T_LM_CONSULTA_VIAGEM (ID_USUARIO, ID_ESTACAO_ORIGEM, ID_ESTACAO_DESTINO, DS_SENTIDO, DT_VIAGEM,
                                          HR_DESEJADO, HR_SUGERIDO, NR_INDICE_DESEJADO, NR_INDICE_SUGERIDO,
                                          ID_ESTACAO_CRITICA, ST_SUGESTAO_ACEITA, DT_CONSULTA)
        SELECT u.ID_USUARIO, o.ID_ESTACAO, d.ID_ESTACAO, p_sentido, TRUNC(CAST(v_quando AS DATE)),
               p_desejado, p_sugerido, p_ind_des, p_ind_sug, c.ID_ESTACAO, p_aceita, v_quando
          FROM T_LM_USUARIO u, T_LM_ESTACAO o, T_LM_ESTACAO d, T_LM_ESTACAO c
         WHERE u.DS_EMAIL = p_email AND o.NM_ESTACAO = p_origem AND d.NM_ESTACAO = p_destino
           AND c.NM_ESTACAO = p_critica;
    END;
BEGIN
    consulta('carlos.mendes@exemplo.com',   'Artur Alvim',          'Sé',                   'SENTIDO_BARRA_FUNDA', '07:00', '06:00', 96, 50, 'Brás',          1,    120);
    consulta('maria.aparecida@exemplo.com', 'Penha',                'República',            'SENTIDO_BARRA_FUNDA', '07:30', '06:15', 94, 55, 'Brás',          1,    96);
    consulta('juliana.rocha@exemplo.com',   'Tatuapé',              'Anhangabaú',           'SENTIDO_BARRA_FUNDA', '08:00', '06:30', 93, 60, 'Brás',          NULL, 72);
    consulta('pedro.lima@exemplo.com',      'Sé',                   'Corinthians-Itaquera', 'SENTIDO_ITAQUERA',    '18:00', '19:30', 97, 64, 'Brás',          0,    48);
    consulta('ana.souza@exemplo.com',       'Corinthians-Itaquera', 'Brás',                 'SENTIDO_BARRA_FUNDA', '07:15', '06:00', 93, 47, 'Bresser-Mooca', 1,    24);
    consulta('roberto.alves@exemplo.com',   'Brás',                 'Tatuapé',              'SENTIDO_ITAQUERA',    '18:15', '19:45', 96, 58, 'Brás',          1,    2);
END;
/

COMMIT;
