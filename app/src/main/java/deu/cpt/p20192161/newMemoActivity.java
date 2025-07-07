/*
 * 개인프로젝트 - newMemoActivity.java
 * 개발자 : 컴퓨터공학과 20192161 황도균
 * 20192161@office.deu.ac.kr
 */

package deu.cpt.p20192161;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import deu.cpt.p20192161.R;

public class newMemoActivity extends AppCompatActivity {
    private int BACK_PRESS_DELAY = 2000; // 뒤로 가기 버튼 클릭 간의 시간 간격
    private long backPressTime = 0;           // 마지막으로 뒤로 가기 버튼이 눌린 시간을 저장
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userReference;
    private DatabaseReference userPostsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_memo);

        EditText titleEditText = findViewById(R.id.new_memo_titleEdt);
        EditText contentEditText = findViewById(R.id.new_memo_detailEdt);
        Button saveButton = findViewById(R.id.new_memo_saveBtn);
        Button cancelButton = findViewById(R.id.new_memo_cancelBtn);
        Button imageButton = findViewById(R.id.imageBtn);
        TextView imageUrl_tv = findViewById(R.id.imageUrl_tv);

        Intent intent = getIntent();
        String get_imageUrl = intent.getStringExtra("imageUrl_");
        String get_title = intent.getStringExtra("title");
        String get_content = intent.getStringExtra("content");
        imageUrl_tv.setText(get_imageUrl);
        titleEditText.setText(get_title);
        contentEditText.setText(get_content);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        userReference = database.getReference("a20192161").child("UserAccount").child(firebaseUser.getUid());
        userPostsReference = userReference.child("posts");

        // ********************** 저장 버튼 클릭시 *****************************************
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 저장할 값
                String titleEdt = titleEditText.getText().toString();
                String contentEdt = contentEditText.getText().toString();
                String imageTv = imageUrl_tv.getText().toString();
                String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                // 저장 조건
                if (titleEdt.isEmpty() && contentEdt.isEmpty()) {
                    Toast.makeText(newMemoActivity.this, "메모의 '제목'란과 '내용'란이 비워졌습니다.", Toast.LENGTH_SHORT).show();
                } else if (contentEdt.isEmpty()) {
                    Toast.makeText(newMemoActivity.this, "메모의 '내용'란이 비워졌습니다.", Toast.LENGTH_SHORT).show();
                } else if (titleEdt.isEmpty()) {
                    Toast.makeText(newMemoActivity.this, "메모의 '제목'란이 비워졌습니다.", Toast.LENGTH_SHORT).show();
                } else if (imageTv.isEmpty()){
                    Toast.makeText(newMemoActivity.this, "메모의 '이미지'란이 비워졌습니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    userPostsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // 현재 데이터베이스에 있는 게시글 중에서 가장 큰 번호를 찾습니다.
                            int maxPostNumber = 0;
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                int postNumber = Integer.parseInt(postSnapshot.getKey());
                                if (postNumber > maxPostNumber) {
                                    maxPostNumber = postNumber;
                                }
                            }
                            int nextPostNumber = maxPostNumber + 1;
                            String postId = String.valueOf(nextPostNumber);

                            memo_item memoitem = new memo_item();
                            memoitem.setNum(postId);
                            memoitem.setTitle(titleEdt);
                            memoitem.setContent(contentEdt);
                            memoitem.setWriteDate(currentTime);
                            memoitem.setImageUrl(get_imageUrl);

                            userPostsReference.child(postId).setValue(memoitem);
                            Toast.makeText(newMemoActivity.this, "메모가 작성되었습니다.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // 오류 처리
                        }
                    });
                }
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(newMemoActivity.this, DrawingActivity.class);
                intent.putExtra("title", titleEditText.getText().toString());
                intent.putExtra("content", contentEditText.getText().toString());
                startActivity(intent);
                finish();
            }
        });
        // ********************** 취소 버튼 클릭시 *****************************************
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(newMemoActivity.this);
                builder.setMessage("작성 중인 내용을 저장하지 않고 나가시겠습니까?");
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(newMemoActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    // **********************  뒤로가기  ***********************
    @Override
    public void onBackPressed() { // 뒤로 가기 버튼 눌렀을 때 자동 호출
        long currentTime = System.currentTimeMillis();
        // 2초 안에 두 번 클릭 된 경우
        if (currentTime - backPressTime < BACK_PRESS_DELAY) {
            Intent intent = new Intent(newMemoActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            backPressTime = currentTime;
        }
    }
}