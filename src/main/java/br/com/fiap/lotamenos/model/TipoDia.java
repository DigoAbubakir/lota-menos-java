package br.com.fiap.lotamenos.model;

import java.time.DayOfWeek;
import java.time.LocalDate;

public enum TipoDia {
    UTIL,
    SABADO,
    DOMINGO;

    public static TipoDia de(LocalDate data) {
        DayOfWeek dia = data.getDayOfWeek();
        if (dia == DayOfWeek.SATURDAY) {
            return SABADO;
        }
        if (dia == DayOfWeek.SUNDAY) {
            return DOMINGO;
        }
        return UTIL;
    }
}
