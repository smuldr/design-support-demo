package smuldr.designsupportdemo;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

public class FirstFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

  private int mYear = 1984;
  private int mMonthOfYear = 2;
  private int mDayOfMonth = 30;
  private EditText mDateText;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_1, container, false);

    mDateText = (EditText) rootView.findViewById(R.id.date);
    mDateText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showDatePicker();
      }
    });

    rootView.findViewById(R.id.alert).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showAlert();
      }
    });
    return rootView;
  }

  private void showDatePicker() {
    DatePickerDialog dialog =
        new DatePickerDialog(getActivity(), 0, this, mYear, mMonthOfYear, mDayOfMonth);
    DatePicker datePicker = dialog.getDatePicker();
    datePicker.setSpinnersShown(true);
    datePicker.setMinDate(0);
    dialog.show();
  }

  private void showAlert() {
    new AlertDialog.Builder(getActivity())
        .setMessage("Test alert!")
        .setPositiveButton(android.R.string.ok, null)
        .show();
  }

  @Override
  public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
    mYear = year;
    mMonthOfYear = monthOfYear;
    mDayOfMonth = dayOfMonth;

    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, monthOfYear);
    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

    mDateText.setText(DateFormat.format("d MMM, yyyy", cal));
  }
}
