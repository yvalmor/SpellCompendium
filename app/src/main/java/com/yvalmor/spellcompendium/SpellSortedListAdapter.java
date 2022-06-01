package com.yvalmor.spellcompendium;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SpellSortedListAdapter extends RecyclerView.Adapter<SpellViewHolder> {

    SortedList<Spell> spells;
    private final Context context;
    private final LayoutInflater mLayoutInflater;

    public SpellSortedListAdapter(Context context, List<Spell> spells) {
        this.context = context;
        sort(true, "NAME");
        addAll(spells);
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public SpellViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View recyclerViewItem = mLayoutInflater.inflate(
                R.layout.spell_card_view, parent, false);

        return new SpellViewHolder(recyclerViewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull SpellViewHolder holder, int position) {
        Spell spell = spells.get(position);

        holder.spellNameView.setText(spell.name);
        holder.spellSchoolView.setText(spell.school);

        String level = context.getResources().
                getString(R.string.level) + " " + spell.level;

        holder.spellLevelView.setText(level);

        holder.spell = spell;

        holder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, SpellActivity.class);
                        intent.putExtras(Spell.getBundle(spell));
                        context.startActivity(intent);
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return spells.size();
    }

    public void sort(final boolean ascending, final String property){
        spells = new SortedList<>(Spell.class, new SortedList.Callback<Spell>(){
            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(Spell oldItem, Spell newItem) {
                return oldItem.name.equals(newItem.name);
            }

            @Override
            public boolean areItemsTheSame(Spell item1, Spell item2) {
                return item1.name.equals(item2.name);
            }

            @Override
            public int compare(Spell spell1, Spell spell2){
                if (ascending) {
                    if (property.equalsIgnoreCase("LEVEL")) {
                        if (spell1.level == spell2.level)
                            return spell1.name.compareTo(spell2.name);
                        return spell1.level - spell2.level;
                    }
                    else if (property.equalsIgnoreCase("SCHOOL")) {
                        if (spell1.school.equalsIgnoreCase(spell2.school))
                            return spell1.name.compareTo(spell2.name);
                        return spell1.school.compareTo(spell2.school);
                    }
                    return spell1.name.compareTo(spell2.name);
                }
                else {
                    if (property.equalsIgnoreCase("LEVEL")) {
                        if (spell1.level == spell2.level)
                            return spell1.name.compareTo(spell2.name);
                        return spell2.level - spell1.level;
                    }
                    else if (property.equalsIgnoreCase("SCHOOL")) {
                        if (spell1.school.equalsIgnoreCase(spell2.school))
                            return spell1.name.compareTo(spell2.name);
                        return spell2.school.compareTo(spell1.school);
                    }
                    return spell2.name.compareTo(spell1.name);
                }
            }
        });
    }

    public void addAll(List<Spell> spellList) {
        spells.beginBatchedUpdates();
        for (int i = 0; i < spellList.size(); i++)
            spells.add(spellList.get(i));
        spells.endBatchedUpdates();
    }

    public void replaceAll(List<Spell> spellList) {
        clear();
        addAll(spellList);
    }

    public void addAll(SortedList<Spell> spellList) {
        spells.beginBatchedUpdates();
        for (int i = 0; i < spellList.size(); i++)
            spells.add(spellList.get(i));
        spells.endBatchedUpdates();
    }

    public void replaceAll(SortedList<Spell> spellList, final boolean ascending, final String property) {
        clear();
        sort(ascending, property);
        addAll(spellList);
    }

    public void replaceAll(List<Spell> spellList, final boolean ascending, final String property) {
        clear();
        sort(ascending, property);
        addAll(spellList);
    }

    public Spell get(int position){
        return spells.get(position);
    }

    public void clear() {
        spells.beginBatchedUpdates();
        while (spells.size() > 0)
            spells.removeItemAt(spells.size() - 1);
        spells.endBatchedUpdates();
    }
}
