package br.com.eduardo.salaovitinho.banco;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.com.eduardo.salaovitinho.model.Mensagem;

/**
 * Created by Eduardo on 09/03/2018.
 */
public class MensagemDB extends BancoDB {
    private static final String TABELA = "mensagem";

    public MensagemDB(Context context) {
        super(context);
    }

    public long salvar(Mensagem mensagem) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues valores = new ContentValues();
            valores.put("mensagem", mensagem.getMensagem());
            valores.put("telefone", mensagem.getTelefone());
            valores.put("resposta", mensagem.getResposta());
            valores.put("remetente", mensagem.getRemetente());
            valores.put("lido", String.valueOf(mensagem.isLido()));
            return db.insertOrThrow(TABELA, null, valores);
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        finally {
            db.close();
        }
        return 0;
    }

    public long deletar() {
        SQLiteDatabase db = getWritableDatabase();

        try {
            return db.delete(TABELA, null, null);
        } finally {
            db.close();
        }
    }

    public long deletar(Long id) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            return db.delete(TABELA, "id = " + id, null);
        } finally {
            db.close();
        }
    }

    public long deletar(Mensagem mensagem) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            return db.delete(TABELA, "telefone = '" + mensagem.getTelefone() + "'", null);
        } finally {
            db.close();
        }
    }

    public List<Mensagem> buscaUltimaMensagemTelefone(Mensagem mensagem) {
        SQLiteDatabase db = getWritableDatabase();
        List<Mensagem> mensagems = new ArrayList<>();
        try {
            Cursor cursor;
            cursor = db.query(TABELA, null, "telefone = '" + mensagem.getTelefone() + "' AND mensagem = '" + mensagem.getMensagem() + "'" ,null, null, null, "id DESC LIMIT 1");
            mensagems = toList(cursor);
        } catch (RuntimeException e) {
            Log.e("ERRO", e.getMessage());
        }
        catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        } finally {
            db.close();
        }
        return mensagems;
    }

    public List<Mensagem> buscaMensagensTelefone(Mensagem mensagem) {

        SQLiteDatabase db = getWritableDatabase();
        List<Mensagem> mensagems = new ArrayList<>();
        try {
            Cursor cursor = db.query(TABELA, null, "telefone = '" + mensagem.getTelefone() + "'" ,null, null, null, null);
            mensagems = toList(cursor);
        } catch (RuntimeException e) {
            Log.e("ERRO", e.getMessage());
        }
        catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        } finally {
            db.close();
        }
        return mensagems;
    }

    public List<Mensagem> buscaTodosAgendamentos() {
        SQLiteDatabase db = getWritableDatabase();
        List<Mensagem> mensagems = new ArrayList<>();

        try {
            Cursor cursor = db.query(TABELA, null, null ,null, null, null, "id");
            mensagems = toList(cursor);
        } catch (RuntimeException e) {
            Log.e("ERRO", e.getMessage());
        }
        catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        } finally {
            db.close();
        }
        return mensagems;
    }

    public long update(Mensagem mensagem) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues valores = new ContentValues();
            valores.put("mensagem", mensagem.getMensagem());
            valores.put("telefone", mensagem.getTelefone());
            valores.put("resposta", mensagem.getResposta());
            valores.put("remetente", mensagem.getRemetente());
            valores.put("lido", mensagem.isLido());

            return db.update(TABELA, valores, "id = " + mensagem.getId(), null);
        } finally {
            db.close();
        }
    }

    private List<Mensagem> toList(Cursor cursor) {
        List<Mensagem> horarios = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Mensagem horario = new Mensagem();
                horario.setId(cursor.getLong(cursor.getColumnIndex("id")));
                horario.setMensagem(cursor.getString(cursor.getColumnIndex("mensagem")));
                horario.setTelefone(cursor.getString(cursor.getColumnIndex("telefone")));
                horario.setResposta(cursor.getString(cursor.getColumnIndex("resposta")));
                horario.setRemetente(cursor.getString(cursor.getColumnIndex("remetente")));
                horario.setLido(Boolean.valueOf(cursor.getString(cursor.getColumnIndex("lido"))));

                horarios.add(horario);
            } while (cursor.moveToNext());

        }
        return horarios;
    }
}