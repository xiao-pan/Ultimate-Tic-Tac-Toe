package org.example.tictactoe;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.lang.reflect.Field;

public class MainFragment extends Fragment {

    private int selectedHelpIndex = 0;
    private AlertDialog mDialog;
    final String[] arrayHelp={"游戏简介", "游戏规则", "游戏操作"};

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
       View rootView = inflater.inflate(R.layout.fragment_main, container, false);//传入XML布局再将其转换为视图

       View newButton = rootView.findViewById(R.id.new_button);
       View continueButton = rootView.findViewById(R.id.continue_button);
       View settingsButton = rootView.findViewById(R.id.settings_button);
       View helpButton = rootView.findViewById(R.id.help_button);
       View quitButton = rootView.findViewById(R.id.quit_button);

       //处理开始游戏按钮
       newButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Intent intent = new Intent(getActivity(), GameActivity.class);
            getActivity().startActivity(intent);
         }
      });

       //处理继续游戏按钮
       continueButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Intent intent = new Intent(getActivity(), GameActivity.class);
            intent.putExtra(GameActivity.KEY_RESTORE, true);
            getActivity().startActivity(intent);
         }
      });

       //处理设置按钮
       settingsButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             Intent intent = new Intent(getActivity(), SettingActivity.class);
             getActivity().startActivity(intent);
         }
      });

       //处理帮助按钮
       helpButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             AlertDialog.Builder helpBuilder = new AlertDialog.Builder(getActivity());
             helpBuilder.setTitle(R.string.help_title);
             helpBuilder.setCancelable(false);
             //第二个参数默认为0表示选中第一个项目
             helpBuilder.setSingleChoiceItems(arrayHelp, 0, new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     selectedHelpIndex = which;
                 }
             });

             //【确定】按钮处理
             helpBuilder.setPositiveButton(R.string.help_sure,
                     new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialogInterface, int i) {
                             //用反射机制获取相关字段进行设置
                             try {
                                 Field field = dialogInterface.getClass()
                                         .getSuperclass()
                                         .getDeclaredField("mShowing");
                                 field.setAccessible(true);
                                 //   将mShowing变量设为false，表示对话框已关闭 
                                 field.set(dialogInterface, false);
                             } catch (Exception e)
                             {
                                 e.printStackTrace();
                             }
                             helpSelected();//帮助信息选择处理
                         }
                     });

             //【取消】按钮处理
             helpBuilder.setNegativeButton(R.string.help_cancel,
                     new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialogInterface, int i) {
                             try {
                                 Field field = dialogInterface.getClass()
                                         .getSuperclass()
                                         .getDeclaredField("mShowing");
                                 field.setAccessible(true);
                                 //   将mShowing变量设为true，表示对话框未关闭，系统调用dismiss关闭对话框 
                                 field.set(dialogInterface,true);
                             } catch (Exception e)
                             {
                                 e.printStackTrace();
                             }
                         }
                     });
             mDialog = helpBuilder.show();
         }
      });

       //处理退出按钮
      quitButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
             builder.setTitle(R.string.quit_title);
             builder.setMessage(R.string.quit_message);
             builder.setCancelable(false);
             builder.setIcon(android.R.drawable.presence_away);

             //【确定】按钮处理
             builder.setPositiveButton(R.string.quit_sure,
                    new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {
                           android.os.Process.killProcess(android.os.Process.myPid());//退出程序
                       }
                    });

             //【取消】按钮处理
             builder.setNegativeButton(R.string.quit_cancel,
                     new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {
                          // nothing
                       }
                    });
             mDialog = builder.show();
         }
      });
      return rootView;
   }

   //帮助信息选择处理
   public void  helpSelected () {
       switch (arrayHelp[selectedHelpIndex]) {
           case "游戏简介":
               final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
               builder.setTitle(R.string.help_introduction_title);
               builder.setMessage(R.string.help_introduction_text);
               builder.setCancelable(false);
               //【确定】按钮处理
               builder.setPositiveButton(R.string.ok_label,
                       new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {

                           }
                       });
               mDialog = builder.show();
               break;
           case "游戏规则":
               AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
               builder1.setTitle(R.string.help_rule_title);
               builder1.setMessage(R.string.help_rule_text);
               builder1.setCancelable(false);
               //【确定】按钮处理
               builder1.setPositiveButton(R.string.ok_label,
                       new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                           }
                       });
               mDialog = builder1.show();
               break;
           case "游戏操作":
               AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
               builder2.setTitle(R.string.help_operation_title);
               builder2.setMessage(R.string.help_operation_text);
               builder2.setCancelable(false);
               //【确定】按钮处理
               builder2.setPositiveButton(R.string.ok_label,
                       new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {

                           }
                       });
               mDialog = builder2.show();
               break;
       }
   }

//   @Override
//   public void onPause() {
//      super.onPause();
//
//      if (mDialog != null)
//         mDialog.dismiss();
//   }
}

