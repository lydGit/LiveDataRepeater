package lyd.git.livedatarepeater;

import androidx.lifecycle.LifecycleOwner;

public class TestRepeater {

    public TestRepeater(LifecycleOwner owner, final ITestRepeater iRepeater) {
//        viewModel.name.observe(owner, new Observer<String>() {
//            @Override
//            public void onChanged(String s) {
//                i.test(s);
//            }
//        });
//        viewModel.name.observe(owner, new Observer<String>() {
//            @Override
//            public void onChanged(String s) {
//
//            }
//        });
//        viewModel.name.observe(owner, new Observer<String>() {
//            @Override
//            public void onChanged(String s) {
//
//            }
//        });
//        viewModel.name.observe(owner, new Observer<String>() {
//            @Override
//            public void onChanged(String s) {
//
//            }
//        });
//        viewModel.name.observe(owner, new Observer<String>() {
//            @Override
//            public void onChanged(String s) {
//
//            }
//        });
//        viewModel.name.observe(owner, new Observer<String>() {
//            @Override
//            public void onChanged(String s) {
//
//            }
//        });
//        viewModel.name.observe(owner, new Observer<String>() {
//            @Override
//            public void onChanged(String s) {
//
//            }
//        });
    }

    interface ITestRepeater {
        void test(String s);
    }


}
