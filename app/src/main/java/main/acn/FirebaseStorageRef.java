package main.acn;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseStorageRef {
    static StorageReference ref;

    public static void initStorage(){
        ref = FirebaseStorage.getInstance().getReference();
    }

    public static StorageReference getRef(){
        return ref;
    }
}
