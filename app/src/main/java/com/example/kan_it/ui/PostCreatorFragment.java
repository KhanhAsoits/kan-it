package com.example.kan_it.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.kan_it.R;
import com.example.kan_it.core.AlertCore;
import com.example.kan_it.core.Logger;
import com.example.kan_it.databinding.FragmentPostCreatorBinding;
import com.example.kan_it.model.Post;
import com.example.kan_it.service.PostService;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import dev.shreyaspatil.MaterialDialog.AbstractDialog;
import jp.wasabeef.richeditor.RichEditor;

public class PostCreatorFragment extends Fragment {

    FragmentPostCreatorBinding mBinder;
    RichEditor mEditor;

    public boolean isBold = false;
    public boolean isItalic = false;

    public interface IOnPickImageResult {
        public void onResult(Uri data);
    }

    private List<String> uriFile = new ArrayList<>();
    private boolean isCreated = false;
    public static IOnPickImageResult mIOnPickImageResult;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinder = FragmentPostCreatorBinding.inflate(inflater);
        return mBinder.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadEditor();
        mIOnPickImageResult = new IOnPickImageResult() {
            @Override
            public void onResult(Uri data) {
                MainActivity.loader();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference ref = storage.getReference();
                String filename = UUID.randomUUID().toString() + ".png";
                StorageReference imageRef = ref.child(filename);
                UploadTask uploadTask = imageRef.putFile(data);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return imageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri uri = task.getResult();
                            uriFile.add(filename);
                            mEditor.insertImage(uri.toString(), "image", 320);
                        }
                        MainActivity.unLoader();
                    }
                });
            }
        };
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bound();
        regEvent();
    }

    public void bound() {
        MainActivity.mBinder.mainNav.setVisibility(View.INVISIBLE);
        MainActivity.mBinder.mainNav.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
    }

    public void unBound() {
        MainActivity.mBinder.mainNav.setVisibility(View.VISIBLE);
        MainActivity.mBinder.mainNav.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void onDestroy() {
        if (!isCreated) {
            FirebaseStorage storageReference = FirebaseStorage.getInstance();
            StorageReference ref = storageReference.getReference();
            for (String file : uriFile) {
                StorageReference child = ref.child(file);
                child.delete();
            }
        }
        super.onDestroy();
        unBound();
    }

    public void loadEditor() {
        mEditor = mBinder.edtContent;
        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(22);
        mEditor.setEditorFontColor(Color.RED);
        mEditor.setPadding(10, 10, 10, 10);
        // reg event

        mBinder.actionUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.undo();
            }
        });

        mBinder.actionRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.redo();
            }
        });

        mBinder.actionBold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBold();
                isBold = !isBold;
                if (isBold) {
                    mBinder.actionBold.setBackgroundColor(Color.parseColor("#dcdcdc"));
                } else {
                    mBinder.actionBold.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });

        mBinder.actionItalic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setItalic();
                isItalic = !isItalic;
                if (isItalic) {
                    mBinder.actionItalic.setBackgroundColor(Color.parseColor("#dcdcdc"));
                } else {
                    mBinder.actionItalic.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });

        mBinder.actionSubscript.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setSubscript();
            }
        });

        mBinder.actionSuperscript.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setSuperscript();
            }
        });

        mBinder.actionStrikethrough.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setStrikeThrough();
            }
        });

        mBinder.actionUnderline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setUnderline();
            }
        });

        mBinder.actionHeading1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(1);
            }
        });

        mBinder.actionHeading2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(2);
            }
        });

        mBinder.actionHeading3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(3);
            }
        });

        mBinder.actionHeading4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(4);
            }
        });

        mBinder.actionHeading5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(5);
            }
        });

        mBinder.actionHeading6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(6);
            }
        });

        mBinder.actionTxtColor.setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override
            public void onClick(View v) {
                mEditor.setTextColor(isChanged ? Color.BLACK : Color.RED);
                isChanged = !isChanged;
            }
        });

        mBinder.actionBgColor.setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override
            public void onClick(View v) {
                mEditor.setTextBackgroundColor(isChanged ? Color.TRANSPARENT : Color.YELLOW);
                isChanged = !isChanged;
            }
        });

        mBinder.actionIndent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setIndent();
            }
        });

        mBinder.actionOutdent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setOutdent();
            }
        });

        mBinder.actionAlignLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignLeft();
            }
        });

        mBinder.actionAlignCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignCenter();
            }
        });

        mBinder.actionAlignRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignRight();
            }
        });

        mBinder.actionBlockquote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBlockquote();
            }
        });

        mBinder.actionInsertBullets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBullets();
            }
        });

        mBinder.actionInsertNumbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setNumbers();
            }
        });

        mBinder.actionInsertImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mIOpenFileChooser.choseImage();
            }
        });

        mBinder.actionInsertLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = new EditText(requireActivity());
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireActivity());
                alertDialog.setView(editText);
                alertDialog.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        if (!String.valueOf(editText.getText()).equals("")) {
                            mEditor.insertLink(String.valueOf(editText.getText()), String.valueOf(editText.getText()));
                        }
                    }
                });
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
        mBinder.actionInsertCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.insertTodo();
            }
        });
    }

    public void regEvent() {
        mBinder.btnCloseNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinder.txtNotify.setVisibility(View.INVISIBLE);
                mBinder.txtNotify.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
            }
        });
        mBinder.btnCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPost();
            }
        });
    }

    public void showToast(String msg) {
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public void createPost() {
        try {
            String title = String.valueOf(mBinder.edtTitle.getText());
            String tags = String.valueOf(mBinder.edtTag.getText());
            String contents = mEditor.getHtml();
            if (title.equals("")) {
                showToast("tiêu đề không được trống.");
                return;
            }
            if (tags.equals("")) {
                showToast("phải có ít nhất 1 thẻ.");
                return;
            }
            if (contents == null) {
                showToast("nội dung không thể trống.");
                return;
            }
            if (TextUtils.equals(contents, "")) {
                showToast("nội dung không thể trống.");
                return;
            }

            // process tag
            String[] tagsSplit = tags.split(",");
            if (tagsSplit.length < 1 || tagsSplit.length > 5) {
                showToast("số lượng thẻ phải nhỏ hơn 5 và có ít nhất 1 thẻ.");
                return;
            }
            Post post = new Post();
            post.setDesc(contents);
            post.setTitle(title);
            post.setTagIds(Arrays.asList(tagsSplit));
            PostService.gI().createPost(post, new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()) {
                        showToast("Tạo vài viết thành công , đợi admin duyệt.");
                        mBinder.edtTag.setText("");
                        mBinder.edtTitle.setText("");
                        mBinder.edtContent.reload();
                        isCreated = true;
                        onDestroy();
                        MainActivity.mMenuController.navigate(R.id.homeFragment);
                    }
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showToast("Có lỗi xảy ra , vui lòng thử lại");
                }
            });
        } catch (Exception e) {
            Log.d(Logger.TAG_LOG, e.getMessage());
        }
    }

    public static final int FILE_SELECT_CODE = 0;


}