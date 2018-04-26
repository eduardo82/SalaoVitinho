package br.com.eduardo.salaovitinho.util;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import br.com.eduardo.salaovitinho.constatns.SalaoVitinhoConstants;
import br.com.eduardo.salaovitinho.model.Horario;

/**
 * Created by eduardo.vasconcelos on 30/10/2017.
 */

public class FirebaseUtils {

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static DatabaseReference getReferenceChild(String... children) {
        DatabaseReference reference = database.getReference();
        for (String child : children) {
            reference = reference.getRef().child(child);
        }
        return reference;
    }
}
