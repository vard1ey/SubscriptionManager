package com.example.subscriptionmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.subscriptionmanager.database.DatabaseHelper;
import com.example.subscriptionmanager.models.Subscription;

import java.text.DecimalFormat;
import java.util.List;

public class SubscriptionAdapter extends ArrayAdapter<Subscription> {
    private Context context;
    private List<Subscription> subscriptions;
    private DecimalFormat df = new DecimalFormat("#.##");
    private DatabaseHelper dbHelper;

    public SubscriptionAdapter(Context context, List<Subscription> subscriptions) {
        super(context, 0, subscriptions);
        this.context = context;
        this.subscriptions = subscriptions;
        this.dbHelper = new DatabaseHelper(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_subscription, parent, false);
        }

        final Subscription subscription = subscriptions.get(position);
        final int pos = position;

        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvCategory = convertView.findViewById(R.id.tvCategory);
        TextView tvPrice = convertView.findViewById(R.id.tvPrice);
        TextView tvDates = convertView.findViewById(R.id.tvDates);
        Button btnDelete = convertView.findViewById(R.id.btnDelete);

        tvName.setText(subscription.getName());
        tvCategory.setText("Категория: " + subscription.getCategory());
        tvPrice.setText("₽" + df.format(subscription.getPrice()) + " в месяц");
        tvDates.setText("с " + subscription.getStartDate() + " по " + subscription.getEndDate());

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(subscription, pos);
            }
        });

        return convertView;
    }

    private void showDeleteConfirmationDialog(final Subscription subscription, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Удаление подписки");
        builder.setMessage("Вы уверены, что хотите удалить подписку \"" + subscription.getName() + "\"?");
        builder.setPositiveButton("Удалить", (dialog, which) -> {
            deleteSubscription(subscription, position);
        });
        builder.setNegativeButton("Отмена", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

    private void deleteSubscription(Subscription subscription, int position) {
        if (dbHelper.deleteSubscription(subscription.getId())) {
            subscriptions.remove(position);
            notifyDataSetChanged();
            Toast.makeText(context, "Подписка удалена", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Ошибка при удалении", Toast.LENGTH_SHORT).show();
        }
    }
}