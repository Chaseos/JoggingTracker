package com.joggingtracker;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joggingtracker.data.JogContract;

import java.text.DateFormat;
import java.util.Date;

import static com.joggingtracker.JogActivity.JOG_DATE;

public class PreviousJogsActivity extends AppCompatActivity {

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout mDrawerLayout;
    private Context context;
    private Cursor cursor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.previous_runs);

        context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(drawerToggle);
        NavigationView nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);

        //TODO: Query database for all runs and fill recyclerview with adapter with those views.
        //TODO: On click of item place id in intent and pass onto RunFinished
        cursor = getContentResolver().query(JogContract.JogEntry.CONTENT_URI,
                null, null, null,
                JogContract.JogEntry.COLUMN_JOG_DATE_TIME + " DESC");
        cursor.moveToFirst();

        Log.d("blahblahcursor", String.valueOf(cursor.getCount()));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.previous_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        JogAdapter jogAdapter = new JogAdapter();
        recyclerView.setAdapter(jogAdapter);
    }

    class JogAdapter extends RecyclerView.Adapter<JogAdapter.JogListViewHolder> {

        @Override
        public JogListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.previous_runs_list_item, parent, false);
            view.setFocusable(true);
            return new JogListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(JogListViewHolder holder, int position) {
            cursor.moveToPosition(position);
            long jogDateMillis = cursor.getLong(
                    (cursor.getColumnIndexOrThrow(JogContract.JogEntry.COLUMN_JOG_DATE_TIME)));
            Date date = new Date(jogDateMillis);
            String jogDateString = DateFormat.getDateInstance().format(date);

            holder.dateTextView.setText(jogDateString);
            holder.milesTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(JogContract.JogEntry.COLUMN_JOG_MILES_LENGTH)));
            holder.timeTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(JogContract.JogEntry.COLUMN_JOG_TIME_LENGTH)));
            holder.paceTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(JogContract.JogEntry.COLUMN_JOG_PACE)));
        }

        @Override
        public int getItemCount() {
            return cursor.getCount();
        }

        public class JogListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            final TextView dateTextView;
            final TextView milesTextView;
            final TextView timeTextView;
            final TextView paceTextView;

            public JogListViewHolder(View itemView) {
                super(itemView);

                dateTextView = itemView.findViewById(R.id.date_textView);
                milesTextView = itemView.findViewById(R.id.miles_textView);
                timeTextView = itemView.findViewById(R.id.time_textView);
                paceTextView = itemView.findViewById(R.id.pace_textView);
            }

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PreviousJogsActivity.this, SingleJogActivity.class);
                long jogDate = cursor.getLong(cursor.getColumnIndexOrThrow(JogContract.JogEntry.COLUMN_JOG_DATE_TIME));
                intent.putExtra(JOG_DATE, jogDate);
                startActivity(intent);
            }
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        selectDrawerItem(item);
                        return true;
                    }
                }
        );
    }

    private void selectDrawerItem(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_run:
                Intent intent = new Intent(PreviousJogsActivity.this, MainActivity.class);
                item.setChecked(true);
                mDrawerLayout.closeDrawers();
                startActivity(intent);
                break;
            default:
                mDrawerLayout.closeDrawers();
                return;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }
}
