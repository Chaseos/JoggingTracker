package odometer.hfad.com.joggingtracker;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class DayListFragment extends Fragment {

    private View rootview;

    public DayListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_day_list, container, false);

        RecyclerView dayGridView = rootview.findViewById(R.id.days_gridview);
        dayGridView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        DayAdapter dayAdapter = new DayAdapter();
        dayGridView.setAdapter(dayAdapter);
        return rootview;
    }

    public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {

        @Override
        public DayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext())
                    .inflate(R.layout.day_list_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(DayAdapter.ViewHolder holder, int position) {
            holder.dayTextView.setText(String.valueOf(position + 1));
        }

        @Override
        public int getItemCount() {
            return 18;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView dayTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                dayTextView = itemView.findViewById(R.id.day_textview);
            }
        }
    }
}
