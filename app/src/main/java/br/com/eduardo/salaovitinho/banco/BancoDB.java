package br.com.eduardo.salaovitinho.banco;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static br.com.eduardo.salaovitinho.constatns.SalaoVitinhoConstants.NOME_BANCO;
import static br.com.eduardo.salaovitinho.constatns.SalaoVitinhoConstants.VERSAO;

/**
 * Created by Eduardo on 18/03/2018.
 */

public class BancoDB extends SQLiteOpenHelper {

    public BancoDB(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.execSQL("create table mensagem(id integer primary key autoincrement," +
                    "remetente text, mensagem text, telefone text, resposta text, lido text)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
