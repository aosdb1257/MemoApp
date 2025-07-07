/*
 * 개인프로젝트 - editMemoActivity.java
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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class editMemoActivity extends AppCompatActivity {
    private int BACK_PRESS_DELAY = 2000; // 뒤로 가기 버튼 클릭 간의 시간 간격
    private long backPressTime = 0;           // 마지막으로 뒤로 가기 버튼이 눌린 시간을 저장
    EditText titleedt, contentedt;
    Button editBtn, cancelBtn;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String get_id;
    private String get_writeday;
    private String get_imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_memo);

        databaseReference = FirebaseDatabase.getInstance().getReference("a20192161");
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        titleedt = findViewById(R.id.title_edt);
        contentedt = findViewById(R.id.content_edt);
        editBtn = findViewById(R.id.finaledit_btn);
        cancelBtn = findViewById(R.id.cancel_btn);

        Intent intent = getIntent();
        get_id = intent.getStringExtra("id_");
        get_writeday = intent.getStringExtra("writeDay_");
        get_imageUri = intent.getStringExtra("imageUri_");
        String get_title = intent.getStringExtra("title_");
        String get_content = intent.getStringExtra("content_");

        setTitle(get_id + "번 메모 수정");
        titleedt.setText(get_title);
        contentedt.setText(get_content);

        // ******************* 버튼 이벤트 *********************************************
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTitle = titleedt.getText().toString();
                String newContent = contentedt.getText().toString();
                String lastcurrentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                if (TextUtils.isEmpty(newTitle) && TextUtils.isEmpty(newContent)) {
                    // 제목과 내용 모두 입력하지 않은 경우
                    Toast.makeText(editMemoActivity.this, "제목과 내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(newTitle)) {
                    // 제목만 입력하지 않은 경우
                    Toast.makeText(editMemoActivity.this, "제목을 입력하세요.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(newContent)) {
                    // 내용만 입력하지 않은 경우
                    Toast.makeText(editMemoActivity.this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(editMemoActivity.this);
                    builder.setMessage("정말 " + get_id +"번 메모를 수정할까요?").setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            memo_item memoItem = new memo_item();
                            memoItem.setNum(get_id);
                            memoItem.setTitle(newTitle);
                            memoItem.setContent(newContent);
                            memoItem.setWriteDate(get_writeday);
                            memoItem.setLastwriteDate(lastcurrentTime);
                            memoItem.setImageUrl(get_imageUri);
                            databaseReference.child("UserAccount").child(firebaseUser.getUid()).child("posts").child(get_id).setValue(memoItem);
                            Toast.makeText(editMemoActivity.this, "수정 완료했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {dialog.dismiss();}
                    }).show();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(editMemoActivity.this);
                builder.setMessage("현재 작성하는 메모를 취소하시겠습니까?");
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseReference.child("UserAccount").child(firebaseUser.getUid()).child("posts").child(get_id)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        memo_item memoItem = snapshot.getValue(memo_item.class);
                                        if (memoItem != null) {
                                            Intent editIntent = new Intent(editMemoActivity.this, checkMemoActivity.class);
                                            editIntent.putExtra("id_", get_id);
                                            editIntent.putExtra("writeDay_", memoItem.getWriteDate());
                                            editIntent.putExtra("imageUri_", get_imageUri);
                                            finish();
                                            startActivity(editIntent);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
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
        if (currentTime - backPressTime < BACK_PRESS_DELAY) {
            Intent editIntent = new Intent(editMemoActivity.this, checkMemoActivity.class);
            editIntent.putExtra("id_", get_id);
            editIntent.putExtra("writeDay_", get_writeday);
            editIntent.putExtra("imageUri_", get_imageUri);
            finish();
            startActivity(editIntent);
        } else {
            Toast.makeText(this, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            backPressTime = currentTime;
        }
    }

}