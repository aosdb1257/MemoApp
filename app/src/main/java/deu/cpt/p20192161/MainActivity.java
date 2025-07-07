/*
 * 개인프로젝트 - MainActivity.java
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import deu.cpt.p20192161.R;

public class MainActivity extends AppCompatActivity  {
    private int BACK_PRESS_DELAY = 2000; // 뒤로 가기 버튼 클릭 간의 시간 간격
    private long backPressTime = 0;           // 마지막으로 뒤로 가기 버튼이 눌린 시간을 저장
    MemoAdapter memoAdapter;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("내 메모");
        // 바인딩
        databaseReference = FirebaseDatabase.getInstance().getReference("a20192161");
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        ImageButton btnsearch = (ImageButton) findViewById(R.id.searchBtn);
        EditText searchEditText = findViewById(R.id.search_edt);
        ListView memoListView = findViewById(R.id.memolv);

        // db 데이터 가져오기
        ArrayList<memo_item> memoItems = new ArrayList<>();
        if (firebaseUser != null) {
            // FirebaseUser가 null이 아닌 경우에만 데이터베이스에서 데이터를 가져오기
            databaseReference.child("UserAccount").child(firebaseUser.getUid()).child("posts")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            memoItems.clear(); // 기존 데이터를 비우고 다시 채우기
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                memo_item memoItem = postSnapshot.getValue(memo_item.class);
                                if (memoItem != null) {
                                    memoItems.add(memoItem);
                                }
                            }
                            memoAdapter = new MemoAdapter(MainActivity.this, memoItems);
                            memoListView.setAdapter(memoAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // 에러 처리
                        }
                    });
        } else {
            // FirebaseUser가 null인 경우, 리스트뷰에 아무것도 표시하지 않음
            memoItems.clear();
            memoAdapter = new MemoAdapter(MainActivity.this, memoItems);
            memoListView.setAdapter(memoAdapter);
        }

        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = searchEditText.getText().toString().trim();

                // 검색어가 없는 경우
                if (searchQuery.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                databaseReference.child("UserAccount").child(firebaseUser.getUid())
                        .child("posts").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ArrayList<memo_item> searchResults = new ArrayList<>();
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    memo_item memoItem = postSnapshot.getValue(memo_item.class);
                                    if (memoItem != null) {
                                        // title 또는 content 중 하나라도 검색어와 일치하는 경우를 찾음
                                        if (memoItem.getTitle() != null && memoItem.getTitle().contains(searchQuery)) {
                                            searchResults.add(memoItem);
                                        } else if (memoItem.getContent() != null && memoItem.getContent().contains(searchQuery)) {
                                            searchResults.add(memoItem);
                                        }
                                    }
                                }
                                if (searchResults.isEmpty()) {
                                    Toast.makeText(getApplicationContext(), "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                                }
                                memoAdapter.clear();
                                memoAdapter.addAll(searchResults);
                                memoAdapter.notifyDataSetChanged();
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // 오류 처리
                                Toast.makeText(getApplicationContext(), "검색 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // ******************* ListView Item Click *********************************
        memoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                memo_item selectedMemo = (memo_item) parent.getItemAtPosition(position);

                if(selectedMemo.getPwd() != null){
                    AlertDialog.Builder passwordDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    View passwordDialogView = getLayoutInflater().inflate(R.layout.password_dialog_layout, null);
                    EditText passwordEditText = passwordDialogView.findViewById(R.id.pwdEdt);

                    passwordDialogBuilder.setView(passwordDialogView);
                    passwordDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference.child("UserAccount").child(firebaseUser.getUid()).child("posts").child(selectedMemo.getNum())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            memo_item memoItem = snapshot.getValue(memo_item.class);
                                            String password = passwordEditText.getText().toString();
                                            if(memoItem.getPwd().equals(password)){
                                                Intent checkIntent = new Intent(MainActivity.this, checkMemoActivity.class);
                                                checkIntent.putExtra("id_",selectedMemo.getNum());
                                                checkIntent.putExtra("title_", selectedMemo.getTitle());
                                                checkIntent.putExtra("content_", selectedMemo.getContent());
                                                checkIntent.putExtra("writeDay_", selectedMemo.getWriteDate());
                                                checkIntent.putExtra("imageUri_", selectedMemo.getImageUrl());
                                                finish();
                                                startActivity(checkIntent);
                                            } else{
                                                Toast.makeText(MainActivity.this,"비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                        }
                    });
                    passwordDialogBuilder.setNegativeButton("취소", null);
                    passwordDialogBuilder.show();
                } else{
                    Intent checkIntent = new Intent(MainActivity.this, checkMemoActivity.class);
                    checkIntent.putExtra("id_",selectedMemo.getNum());
                    checkIntent.putExtra("title_", selectedMemo.getTitle());
                    checkIntent.putExtra("content_", selectedMemo.getContent());
                    checkIntent.putExtra("writeDay_", selectedMemo.getWriteDate());
                    checkIntent.putExtra("imageUri_", selectedMemo.getImageUrl());
                    finish();
                    startActivity(checkIntent);
                }
            }
        });
        memoListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                memo_item selectedMemo = (memo_item) parent.getItemAtPosition(position);

                if(selectedMemo.getPwd() != null){
                    return true;
                } else{
                    AlertDialog.Builder passwordDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    View passwordDialogView = getLayoutInflater().inflate(R.layout.password_dialog_layout, null);
                    EditText passwordEditText = passwordDialogView.findViewById(R.id.pwdEdt);

                    passwordDialogBuilder.setView(passwordDialogView);
                    passwordDialogBuilder.setPositiveButton("등록", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String password = passwordEditText.getText().toString();
                            selectedMemo.setPwd(password);
                            databaseReference.child("UserAccount").child(firebaseUser.getUid()).child("posts")
                                    .child(selectedMemo.getNum()).setValue(selectedMemo);
                        }
                    });
                    passwordDialogBuilder.setNegativeButton("취소", null);
                    passwordDialogBuilder.show();
                    return true;
                }
            }
        });
    }

    // **********************  옵션 메뉴  ***********************
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.menu_new_memo==item.getItemId()){
            Intent newMemoIntent = new Intent(this, newMemoActivity.class);
            finish();
            startActivity(newMemoIntent);
        }
        else if(R.id.menu_dev_info==item.getItemId()){
            AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
            dlg.setTitle("개발자 정보");
            dlg.setMessage("동의대학교 컴퓨터공학과 황도균.\n(20192161@office.deu.ac.kr)");
            dlg.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this,"확인을 누르셨습니다.",Toast.LENGTH_SHORT).show();
                }
            });
            dlg.show();
        }else if(R.id.menu_logout==item.getItemId()){
            Intent logoutIntent = new Intent(MainActivity.this, LogoutActivity.class);
            startActivity(logoutIntent);
            finish();
        }
        return false;
    }
    // **********************  뒤로가기  ***********************
    @Override
    public void onBackPressed() { // 뒤로 가기 버튼 눌렀을 때 자동 호출
        long currentTime = System.currentTimeMillis();
        // 2초 안에 두 번 클릭 된 경우
        if (currentTime - backPressTime < BACK_PRESS_DELAY) {
            finish();
        } else {
            Toast.makeText(this, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            backPressTime = currentTime;
        }
    }
}