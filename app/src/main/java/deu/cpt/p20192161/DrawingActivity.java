/*
 * 개인프로젝트 - DrawingActivity.java
 * 개발자 : 컴퓨터공학과 20192161 황도균
 * 20192161@office.deu.ac.kr
 */

package deu.cpt.p20192161;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import deu.cpt.p20192161.R;

public class DrawingActivity extends AppCompatActivity {
    private int BACK_PRESS_DELAY = 2000; // 뒤로 가기 버튼 클릭 간의 시간 간격
    private long backPressTime = 0;           // 마지막으로 뒤로 가기 버튼이 눌린 시간을 저장
    private DrawingView drawingView;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    String get_title;
    String get_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        drawingView = new DrawingView(this);
        LinearLayout linearLayout = findViewById(R.id.DrawingLinear);
        linearLayout.addView(drawingView);

        Intent intent = getIntent();
        get_title = intent.getStringExtra("title");
        get_content = intent.getStringExtra("content");

        ImageButton clearBtn = (ImageButton) findViewById(R.id.clearBtn);
        ImageButton saveBtn = (ImageButton) findViewById(R.id.saveBtn);
        ImageButton backBtn = (ImageButton) findViewById(R.id.backBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userId = firebaseUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("UserAccount").child(userId);

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.clearCanvas();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDrawingToFirebase();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DrawingActivity.this, newMemoActivity.class);
                intent.putExtra("title",get_title);
                intent.putExtra("content",get_content);
                startActivity(intent);
                finish();
            }
        });

    }
    private void saveDrawingToFirebase() {
        // 이미지를 바이트 배열로 변환
        Bitmap bitmap = drawingView.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Firebase Storage에 이미지 업로드
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String postId = databaseReference.child("posts").push().getKey();
        StorageReference imageRef = storageRef.child("images/" + postId + ".jpg");

        ProgressDialog progressDialog = new ProgressDialog(DrawingActivity.this);
        progressDialog.setMessage("Uploading Image...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        imageRef.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // 이미지 업로드 성공
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUrl) {
                                // 다운로드 URL 성공적으로 얻음
                                memo_item memoItem = new memo_item();
                                String imageUrl = downloadUrl.toString();
                                Intent intent = new Intent(DrawingActivity.this, newMemoActivity.class);
                                intent.putExtra("imageUrl_",imageUrl);
                                intent.putExtra("title",get_title);
                                intent.putExtra("content",get_content);
                                startActivity(intent);
                                finish();
                                Toast.makeText(DrawingActivity.this, "그림 메모가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 이미지 업로드 실패
                        Toast.makeText(DrawingActivity.this, "이미지 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    // **********************  뒤로가기  ***********************
    @Override
    public void onBackPressed() { // 뒤로 가기 버튼 눌렀을 때 자동 호출
        long currentTime = System.currentTimeMillis();
        // 2초 안에 두 번 클릭 된 경우
        if (currentTime - backPressTime < BACK_PRESS_DELAY) {
            Intent intent = new Intent(DrawingActivity.this, newMemoActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            backPressTime = currentTime;
        }
    }
}