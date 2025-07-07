/*
 * 개인프로젝트 - checkMemoActivity.java
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import deu.cpt.p20192161.R;

public class checkMemoActivity extends AppCompatActivity  {
    private int BACK_PRESS_DELAY = 2000; // 뒤로 가기 버튼 클릭 간의 시간 간격
    private long backPressTime = 0;           // 마지막으로 뒤로 가기 버튼이 눌린 시간을 저장
    TextView titletv, contenttv, writeDatetv;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String get_id;
    private String get_writeDay;
    private String get_imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_memo);

        // 바인딩
        titletv = findViewById(R.id.title_tv);
        contenttv = findViewById(R.id.content_tv);
        writeDatetv = findViewById(R.id.writeDate_tv);
        databaseReference = FirebaseDatabase.getInstance().getReference("a20192161");
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        // 인텐트에서 메모 ID 가져오기
        Intent intent = getIntent();
        get_id = intent.getStringExtra("id_");
        get_writeDay = intent.getStringExtra("writeDay_");
        get_imageUri = intent.getStringExtra("imageUri_");
        setTitle(get_id + "번 메모");
        writeDatetv.append("\n"+get_writeDay);

        databaseReference.child("UserAccount").child(firebaseUser.getUid()).child("posts").child(get_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        memo_item memoItem = snapshot.getValue(memo_item.class);
                        titletv.setText(memoItem.getTitle());
                        contenttv.setText(memoItem.getContent());
                        if (memoItem.getLastwriteDate() != null) {
                            writeDatetv.append("\nLASTWRITEDAY\n" + memoItem.getLastwriteDate());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // ********************** 버튼 ***********************************
        Button editButton = findViewById(R.id.edit_btn);
        Button deleteButton = findViewById(R.id.delete_btn);
        Button backButton = findViewById(R.id.back_btn);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(checkMemoActivity.this, editMemoActivity.class);
                editIntent.putExtra("id_", get_id);
                editIntent.putExtra("title_",titletv.getText().toString());
                editIntent.putExtra("content_", contenttv.getText().toString());
                editIntent.putExtra("writeDay_",get_writeDay);
                editIntent.putExtra("imageUri_", get_imageUri);
                startActivity(editIntent);
                finish();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AlertDialog를 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(checkMemoActivity.this);
                builder.setMessage("이 메모("+get_id+"번)을 삭제하시겠습니까?");

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseReference.child("UserAccount").child(firebaseAuth.getUid()).child("posts").child(get_id).removeValue();
                        Toast.makeText(getApplicationContext(), "삭제됨", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(checkMemoActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(checkMemoActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    // **********************  뒤로가기  ***********************
    @Override
    public void onBackPressed() { // 뒤로 가기 버튼 눌렀을 때 자동 호출
        long currentTime = System.currentTimeMillis();
        // 2초 안에 두 번 클릭 된 경우
        if (currentTime - backPressTime < BACK_PRESS_DELAY) {
            Intent intent = new Intent(checkMemoActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            backPressTime = currentTime;
        }
    }
}