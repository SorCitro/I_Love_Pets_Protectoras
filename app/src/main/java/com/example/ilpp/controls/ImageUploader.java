package com.example.ilpp.controls;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.ilpp.R;
import com.example.ilpp.classes.Message;
import com.example.ilpp.classes.Utils;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ImageUploader extends LinearLayout {

    public static final int GALLERY_INTENT = 1;


    public ImageUploader(Context context) {
        super(context);
        init();
    }

    public ImageUploader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageUploader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = inflate(getContext(), R.layout.control_image_uploader, this);

        iv_Image = view.findViewById(R.id.iv_Image);

        view.setOnClickListener(v -> {
            pickImage();
        });
    }

    private Runnable imageUrlChanged;
    public Runnable getImageUrlChanged() { return imageUrlChanged; }
    public void setImageUrlChanged(Runnable imageUrlChanged) { this.imageUrlChanged = imageUrlChanged; }

    private ShapeableImageView iv_Image;

    private Uri imageUrl;
    public Uri getImageUrl() { return imageUrl; }
    public void setImageUrl(Uri url) {
        this.imageUrl = url;
        Picasso.get().load(url).into(iv_Image);
        // trigger event
        if (getImageUrlChanged() != null) getImageUrlChanged().run();
    }

    private void pickImage() {

        // Preguntar al usuario por la imagen
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Utils.startActivityForResult((FragmentActivity) getContext(), intent, GALLERY_INTENT, null, (requestCode, resultCode, data) -> {
            if (requestCode == GALLERY_INTENT && resultCode == Activity.RESULT_OK) {

                Uri uri = data.getData();
                Utils.uploadImage("animals", uri)
                    .whenComplete((url, error) -> {
                        if (error != null) {
                            Message.exception(getContext(), error, "Error al subir la imagen");
                            return;
                        }

                        this.setImageUrl(url);
                    });

            }
        });
    }

}
