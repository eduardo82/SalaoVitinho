package br.com.eduardo.salaovitinho.formatter;

import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Eduardo on 20/02/2018.
 */

public class DateFormatter {

    private static final String HORA_MINUTO = "HH:mm";
    private static final String DATA_ATUAL_SEM_BARRAS = "dd_MM_yyyy";
    private static final String DATA_ATUAL_COM_BARRAS = "dd/MM/yyyy";
    private static final Locale locale = new Locale("pt", "BR");

    private DateFormatter() {

    }

    public static String formatHoraMinuto(Date date) {
        return new SimpleDateFormat(HORA_MINUTO, locale).format(date);
    }

    public static String getDiaAtual() {
        return new SimpleDateFormat(DATA_ATUAL_SEM_BARRAS, locale).format(new Date());
    }

    public static String getDiaAtualBarras() {
        return new SimpleDateFormat(DATA_ATUAL_COM_BARRAS, locale).format(new Date());
    }

    public static String getDiaAnterior() {
        Calendar diaAnterior = Calendar.getInstance();
        diaAnterior.add(Calendar.DAY_OF_MONTH, -1 );
        return new SimpleDateFormat(DATA_ATUAL_SEM_BARRAS, locale).format(diaAnterior.getTime());
    }
}
