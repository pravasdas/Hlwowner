package oditek.com.hlw;

import android.widget.RadioButton;

class BookNowModel{
    public String mName;
    public RadioButton mRadio;

    public BookNowModel(RadioButton radio){
        mRadio=radio;
    }
    public BookNowModel(String name){
        mName = name;
    }
}

