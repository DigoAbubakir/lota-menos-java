package br.com.fiap.lotamenos.bo;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilitário de horários no formato "HH:mm" e de faixas de 15 minutos.
 */
public final class HorarioUtil {

    public static final int MINUTOS_FAIXA = 15;
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("HH:mm");

    private HorarioUtil() {
    }

    /** Converte "HH:mm" em LocalTime. */
    public static LocalTime parse(String hhmm) {
        return LocalTime.parse(hhmm, FORMATO);
    }

    /** Formata como "HH:mm". */
    public static String formatar(LocalTime horario) {
        return horario.format(FORMATO);
    }

    /** Faixa de 15 min do horário, arredondando para baixo. Ex.: 07:08 → "07:00", 07:15 → "07:15". */
    public static String faixaDe(LocalTime horario) {
        int minutos = minutosDoDia(horario);
        int inicioFaixa = (minutos / MINUTOS_FAIXA) * MINUTOS_FAIXA;
        return formatar(deMinutos(inicioFaixa));
    }

    public static int minutosDoDia(LocalTime horario) {
        return horario.getHour() * 60 + horario.getMinute();
    }

    public static LocalTime deMinutos(int minutosDoDia) {
        return LocalTime.of(minutosDoDia / 60, minutosDoDia % 60);
    }
}
