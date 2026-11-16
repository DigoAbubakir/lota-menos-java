package br.com.fiap.lotamenos.bo;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class HorarioUtil {
    public static final int MINUTOS_FAIXA = 15;
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("HH:mm");

    private HorarioUtil() {
    }

    public static LocalTime parse(String hhmm) {
        return LocalTime.parse(hhmm, FORMATO);
    }

    public static String formatar(LocalTime horario) {
        return horario.format(FORMATO);
    }

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
