package org.example.tictactoe;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class Tile {

   public enum Owner {
      X, O , NEITHER, BOTH
   }

   // 这些级别是在drawable中定义的
   private static final int LEVEL_X = 0;
   private static final int LEVEL_O = 1;
   private static final int LEVEL_BLANK = 2;
   private static final int LEVEL_AVAILABLE = 3;
   private static final int LEVEL_TIE = 3;
   private static final String TAG = "Tile";

   private final GameFragment mGame;
   private Owner mOwner = Owner.NEITHER;
   private View mView;
   private Tile mSubTiles[];

   public Tile(GameFragment game) {
      this.mGame = game;
   }

   public View getView() {
      return mView;
   }

   public void setView(View view) {
      this.mView = view;
   }

   public Owner getOwner() {
      return mOwner;
   }

   public void setOwner(Owner owner) {
      this.mOwner = owner;
   }

   public Tile[] getSubTiles() {
      return mSubTiles;
   }

   public void setSubTiles(Tile[] subTiles) {
      this.mSubTiles = subTiles;
   }

   //管理drawable状态
   public void updateDrawableState() {
      if (mView == null) return;
      int level = getLevel();
      if (mView.getBackground() != null) {
         mView.getBackground().setLevel(level);
      }
      if (mView instanceof ImageButton) {
         Drawable drawable = ((ImageButton) mView).getDrawable();
         drawable.setLevel(level);
      }
   }

   //确定格子等级
   private int getLevel() {
      int level = LEVEL_BLANK;
      switch (mOwner) {
         case X:
            level = LEVEL_X;
            break;
         case O: // letter O
            level = LEVEL_O;
            break;
         case BOTH:
            level = LEVEL_TIE;
            break;
         case NEITHER:
            level = mGame.isAvailable(this) ? LEVEL_AVAILABLE : LEVEL_BLANK;
            break;
      }
      return level;
   }

   //确定小棋盘占据者
   public Owner findWinner() {

      //小棋盘是否已经被占据
      if (getOwner() != Owner.NEITHER)
         return getOwner();

      int totalX[] = new int[4];
      int totalO[] = new int[4];
      countCaptures(totalX, totalO);
      if (totalX[3] > 0) return Owner.X;
      if (totalO[3] > 0) return Owner.O;

      // 是否打成平局
      int total = 0;
      for (int row = 0; row < 3; row++) {
         for (int col = 0; col < 3; col++) {
            Owner owner = mSubTiles[3 * row + col].getOwner();
            if (owner != Owner.NEITHER) total++;
         }
         if (total == 9) return Owner.BOTH;
      }

      // 小棋盘未分胜负
      return Owner.NEITHER;
   }

   //判断是否存在3各棋子排成一条线
   private void countCaptures(int totalX[], int totalO[]) {
      int capturedX, capturedO;

      // 判断是否存在同一个玩家的3个棋子排成一行
      for (int row = 0; row < 3; row++) {
         capturedX = capturedO = 0;
         for (int col = 0; col < 3; col++) {
            Owner owner = mSubTiles[3 * row + col].getOwner();
            if (owner == Owner.X || owner == Owner.BOTH) capturedX++;
            if (owner == Owner.O || owner == Owner.BOTH) capturedO++;
         }
         totalX[capturedX]++;
         totalO[capturedO]++;
      }

      // 判断是否存在同一个玩家的3个棋子排成一列
      for (int col = 0; col < 3; col++) {
         capturedX = capturedO = 0;
         for (int row = 0; row < 3; row++) {
            Owner owner = mSubTiles[3 * row + col].getOwner();
            if (owner == Owner.X || owner == Owner.BOTH) capturedX++;
            if (owner == Owner.O || owner == Owner.BOTH) capturedO++;
         }
         totalX[capturedX]++;
         totalO[capturedO]++;
      }

      // 判断是否存在同一个玩家的3个棋子排成对角线
      capturedX = capturedO = 0;
      for (int diag = 0; diag < 3; diag++) {
         Owner owner = mSubTiles[3 * diag + diag].getOwner();
         if (owner == Owner.X || owner == Owner.BOTH) capturedX++;
         if (owner == Owner.O || owner == Owner.BOTH) capturedO++;
      }
      totalX[capturedX]++;
      totalO[capturedO]++;
      capturedX = capturedO = 0;
      for (int diag = 0; diag < 3; diag++) {
         Owner owner = mSubTiles[3 * diag + (2 - diag)].getOwner();
         if (owner == Owner.X || owner == Owner.BOTH) capturedX++;
         if (owner == Owner.O || owner == Owner.BOTH) capturedO++;
      }
      totalX[capturedX]++;
      totalO[capturedO]++;
   }

   //下棋评估
   public int evaluate (){
      switch (getOwner()){
         case X:
            return 100;
         case O:
            return -100;
         case NEITHER:
            int total = 0;
            if (getSubTiles()!= null){
               //遍历目标小棋盘可下棋的格子
               for (int tile = 0; tile < 9; tile++){
                  total += getSubTiles()[tile].evaluate();
                  Log.d(TAG, "evaluate: 第" +  tile + "个格子的估值为"+ total);
               }
               int totalX[] = new int[4];
               int totalO[] = new int[4];
               countCaptures(totalX, totalO);
               total = total * 1 + totalX[1] + 2 * totalX[2] + 3 *
                       totalX[3] - totalO[1] - 2 * totalO[2] - 3 * totalO[3];
               Log.d(TAG, "evaluate:" + total);
            }
            return total;
      }
      return 0;
   }

   //复制棋盘
   public Tile deepCopy() {
      Tile tile = new Tile(mGame);
      tile.setOwner(getOwner());
      if (getSubTiles() != null) {
         Tile newTiles[] = new Tile[9];
         Tile oldTiles[] = getSubTiles();
         for (int child = 0; child < 9; child++) {
            newTiles[child] = oldTiles[child].deepCopy();;
         }
         tile.setSubTiles(newTiles);
      }
      return tile;
   }
}
