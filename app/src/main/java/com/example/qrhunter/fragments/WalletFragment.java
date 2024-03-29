package com.example.qrhunter.fragments;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.qrhunter.MainActivity;
import com.example.qrhunter.WalletCustomList;
import com.example.qrhunter.QRCode;
import com.example.qrhunter.qrProfile.QRProfileActivity;
import com.example.qrhunter.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * This is a class for the fragment that shows the QRCodes in the database, allows us to remove a QRCode, shows the total no. scanned and total points, sorts QRCodes according to score and allows to add a QRCode.
 */
public class WalletFragment extends Fragment {
    RadioGroup radioGroup;
    Context mContext;
    RadioButton descendingSort, ascendingSort;
    ListView qrList;
    ArrayAdapter<QRCode> qrAdapter;
    ArrayList<QRCode> qrDataList;
    TextView totalPoints;
    TextView totalScanned;
    String userName;

    FloatingActionButton scanButton;
    final String TAG = "Sample";
    FirebaseFirestore db;

    /**
     * Default wallet fragment initializer
     */
    public WalletFragment() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    /**
     * Wallet fragment initializer
     * @param name String object representing player username
     */
    public WalletFragment(String name) {
        userName = name;
    }

    /**
     * Called to create the view hierarchy associated with the fragment. This method is responsible for
     * inflating the fragment's layout and returning the root View of the inflated layout. If the fragment
     * does not have a UI or does not need to display a view, you can return null from this method.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to. This value may be null
     *                           if the fragment is not being attached to a parent view.
     * @param savedInstanceState A Bundle containing any saved state information for the fragment. This value may be null
     *                           if the fragment is being instantiated for the first time.
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        descendingSort = view.findViewById(R.id.rb_descending);
        ascendingSort = view.findViewById(R.id.rb_ascending);
        radioGroup = view.findViewById(R.id.rg_sort);

        totalScanned = view.findViewById(R.id.user_high_score);
        totalPoints = view.findViewById(R.id.user_name_text);

        qrList = view.findViewById(R.id.qr_list);
        qrDataList = new ArrayList<>();
        qrAdapter = new WalletCustomList(this.getActivity(), qrDataList);
        qrList.setAdapter(qrAdapter);
        scanButton = view.findViewById(R.id.wallet_button_scan);

        db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("CodeList");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            /**
             * This method is called when the data in the specified query snapshot changes. This includes
             * the initial data and any subsequent changes to the documents that match the query criteria.
             * Clears the arraylist of QRCodes and adds again from the document.
             *
             * @param value The query snapshot representing the updated data.
             * @param error The error, if any, that occurred while listening for changes. If the error is null,
             *              no errors occurred during the listening operation.
             */
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                qrDataList.clear();
                //Log.d("Check name", userName);

                for (QueryDocumentSnapshot doc: value) {
                    String ownerName = (String) doc.getData().get("owner");
                    if (ownerName != null) {
                        if ((MainActivity.DEBUG_ROY) ? (ownerName.equals("Roy")) :
                                (ownerName.equals(userName))) {
                            Log.d(TAG, "Show list of QR codes");
                            String id = doc.getId();
                            String date = (String) doc.getData().get("date");
                            String hash = (String) doc.getData().get("hash");
                            GeoPoint location = (GeoPoint) doc.getData().get("location");
                            String name = (String) doc.getData().get("name");

                            String owner = (String) doc.getData().get("owner");
                            int score = Integer.parseInt(String.valueOf(doc.getData().get("score")));


                            qrDataList.add(new QRCode(date, hash, name, location, owner, score, id));
                        }
                    }
                }
                qrAdapter.notifyDataSetChanged();
                totalPoints.setText(Integer.toString(countPoints(qrDataList)));
                totalScanned.setText(Integer.toString(qrDataList.size()));

                // Get a reference to the Users collection in the database
                CollectionReference usersRef = FirebaseFirestore.getInstance().collection("Users");

                // Build a query to find the document with the given owner name
                Query query = usersRef.whereEqualTo("UserName", userName);

                // Execute the query asynchronously
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Loop through the documents returned by the query
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get the current score from the document

                            // Calculate the new score by adding the points earned from the QR data
                            int newScore = countPoints(qrDataList);

                            // Update the score field in the document with the new score
                            document.getReference().update("score", newScore)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Score updated for owner: " + userName);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w(TAG, "Error updating score for owner: " + userName, e);
                                    });
                        }
                    } else {
                        Log.w(TAG, "Error getting documents for owner: " + userName, task.getException());
                    }
                });


            }
        });
        scanButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when the scan button is clicked and replaces the fragment with a scanner fragment.
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                ScannerFragment scannerFragment = new ScannerFragment(userName);
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, scannerFragment);
                fragmentTransaction.commit();

            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            /**
             * Called when the checked radio button has changed in a radio group. Sorts the datalist according to the radio button checked. (Either ascending or descending)
             * @param radioGroup The radio group in which the checked radio button has changed.
             * @param i The unique identifier of the newly checked radio button.
             */
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb_ascending) {
                    Collections.sort(qrDataList, QRCode.ascendingOrder);
                    qrAdapter.notifyDataSetChanged();
                } else if (i == R.id.rb_descending) {
                    Collections.sort(qrDataList, QRCode.descendingOrder);
                    qrAdapter.notifyDataSetChanged();
                }
            }
        });

        qrList.setOnItemLongClickListener((parent, v, position, id) -> {
            new AlertDialog.Builder(this.getActivity())
                    .setTitle("Do you want to delete this QR code?")
                    .setPositiveButton("Confirm", (dialog, which) -> {
                        QRCode code = qrAdapter.getItem(position);
                        String docID = code.getId();
                        int score = code.getScore();
                        db.collection("Users").document(Settings.Secure.getString(
                                        mContext.getContentResolver(),
                                        Settings.Secure.ANDROID_ID)).get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        Map<String, Object> userData = documentSnapshot.getData();
                                        if (userData != null) {
                                            userData.put("score",
                                                    (userData.containsKey("score")) ?
                                                            ((Math.toIntExact(
                                                                    (long)userData.get("score"))
                                                            ) - score) : score);
                                            db.collection("Users").document(
                                                    Settings.Secure.getString(
                                                            mContext.getContentResolver(),
                                                            Settings.Secure.ANDROID_ID)).set(userData);
                                        }
                                    }
                                });
                        deleteData(docID);
                        deleteImageFromStorage(docID);
                  //      String folderName = MediaStore.Images.Media.RELATIVE_PATH + '/' +  "Pictures/" + getString(R.string.app_name) + '/' + docID;
                  //      System.out.println("!!!!!! folderName = "+ folderName);
                   //     File fileOrDirectory = new File(folderName);
                        deleteRecursive(docID);
                        deleteComments(docID);
                        radioGroup.clearCheck();
                        qrAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        });

        qrList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * Called when an item in the ListView has been clicked. Opens a new activity for the profile of the QR code clicked.
             * @param adapterView The AdapterView where the click happened.
             * @param view The view within the AdapterView that was clicked (this will be a view provided by the adapter).
             * @param i The position of the view in the adapter.
             * @param l The row id of the item that was clicked.
             */
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), QRProfileActivity.class);
                intent.putExtra("DOC_ID", qrDataList.get(i).getId());
                intent.putExtra("OWNER_NAME", qrDataList.get(i).getOwner());
                startActivity(intent);
            }
        });

        return view;
    }

    private void deleteData(String id){
        db.collection("CodeList").document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });

    }

    public int countPoints(ArrayList<QRCode> qrDataList) {
        int points = 0;
        for (int i = 0; i < qrDataList.size(); i++) {
            points += qrDataList.get(i).getScore();
        }
        return points;
    }


    /**
     * Delete image stored when deleting the qr code
     * @param docId
     * The id of QR code.
     */
    public void deleteImageFromStorage(String docId) {
        String url = "images/" + docId;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child(url).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                int countImage = 0;
                for (StorageReference item : listResult.getItems()) {
                    countImage = listResult.getItems().size();//will give you number of files present in your firebase storage folder
                }
                for (int i = 0; i < countImage; i++) {
                    String imageurl = url + "/image" + String.valueOf(i);
                    deleteImage(imageurl);
                }
            }
        });
    }

    /**
     * Delete a single file of the url position.
     * @param imageurl
     * The url position of the image.
     */
    public void deleteImage(String imageurl){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child(imageurl).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG,"Image storage deleted successfully");
                    }})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Image storage deleted fail"+e);
                    }
                });
    }


    /**
     * Delete image locally from the storage.
     * @param docName
     * The id of the QR code.
     */
    void deleteRecursive(String docName) {
        String url = Environment.getExternalStorageDirectory().toString() + "/Pictures" + '/' + getString(R.string.app_name) + '/' + docName;
        File fileOrDirectory = new File(url);
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                refreshGallery(mContext,child.getPath());

        fileOrDirectory.delete();
    }

    /**
     * Delete a single image and update the gallery.
     * @param context
     * The current context.
     * @param path
     * The path of the image to delete.
     */
    public static void refreshGallery(Context context, String path) {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            file.delete();

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent intent= new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(file));
            context.sendBroadcast(intent);
        } else {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(file.getAbsolutePath())));
        }
    }
    /**
     * Deletes comment from database when deleting the qr code
     * @param docId
     * The id of the QR code.
     */
    public void deleteComments(String docId) {
        db.collection("Comments").document(docId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG,"Comments deleted successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Comments deleted failed."+e);
            }
        });
    }
}