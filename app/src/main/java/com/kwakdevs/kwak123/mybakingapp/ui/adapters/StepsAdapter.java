package com.kwakdevs.kwak123.mybakingapp.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kwakdevs.kwak123.mybakingapp.R;
import com.kwakdevs.kwak123.mybakingapp.data.model.Step;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsViewHolder> {

    private Context context;
    private List<Step> steps;
    private Listener listener;

    public StepsAdapter(Context context, List<Step> steps, Listener listener) {
        this.context = context;
        this.steps = steps;
        this.listener = listener;
    }

    @Override
    public StepsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_step_view, parent, false);
        return new StepsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StepsViewHolder holder, int position) {
        Step step = steps.get(position);
        String stepShortDescription = step.getShortDescription();
        holder.shortDescriptionView.setText(stepShortDescription);
    }

    @Override
    public int getItemCount() {
        if (steps == null) return 0;
        return steps.size();
    }

    class StepsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.step_short_description) TextView shortDescriptionView;

        StepsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onStepClicked(getAdapterPosition());
        }
    }

    public interface Listener {
        void onStepClicked(int stepPosition);
    }
}