package lyd.git.livedatarepeater;

import androidx.lifecycle.MutableLiveData;

import lyd.git.aptannotation.Repeater;
import lyd.git.aptannotation.RepeaterField;

@Repeater
public class MainViewModel {

    String name;

    @RepeaterField(name = "onAge")
    MutableLiveData<String> age;

    @RepeaterField(name = "onText")
    MutableLiveData<Text> text;

    class Text{
        String name;
        String age;
    }
}
