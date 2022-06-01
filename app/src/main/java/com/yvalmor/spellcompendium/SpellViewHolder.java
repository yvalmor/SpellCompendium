package com.yvalmor.spellcompendium;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpellViewHolder extends RecyclerView.ViewHolder {
    public TextView spellNameView,
            spellSchoolView,
            spellLevelView;

    public Spell spell;

    public SpellViewHolder(@NonNull View itemView) {
        super(itemView);

        spellNameView = itemView.findViewById(R.id.spellTitle);
        spellSchoolView = itemView.findViewById(R.id.spellSchool);
        spellLevelView = itemView.findViewById(R.id.spellLevel);
    }
}
