package com.example.criminalintent;

public interface OnItemTouchListener {
    //拖动Item时调用
    void onMove(int fromPosition, int toPosition);
    //滑动Item时调用
    void onSwiped(int position);
}
