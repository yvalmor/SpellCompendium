package com.yvalmor.spellcompendium;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SpellActivity extends AppCompatActivity {

    private void LoadSpell(Spell spell) {
        TextView nameView = findViewById(R.id.spell_name);
        TextView levelView = findViewById(R.id.spell_level);
        TextView schoolView = findViewById(R.id.spell_school);
        TextView rangeView = findViewById(R.id.spell_range);
        TextView areaOfEffectView = findViewById(R.id.spell_area_of_effect);
        TextView componentsView = findViewById(R.id.spell_components);
        TextView materialsView = findViewById(R.id.spell_materials);
        TextView castingTimeView = findViewById(R.id.spell_casting_time);
        TextView attackTypeView = findViewById(R.id.spell_attack_type);
        TextView damageTypeView = findViewById(R.id.spell_damage_type);
        TextView descriptionView = findViewById(R.id.spell_descrition);

        LinearLayout damageLevelLayout = findViewById(R.id.spell_damage_lvl);
        LinearLayout classesLayout = findViewById(R.id.spell_classes);

        nameView.setText(spell.name);

        String level = getResources().getString(R.string.level) + " " + spell.level;
        String nothing = getResources().getString(R.string.nothing);

        levelView.setText(level);
        schoolView.setText(spell.school);
        rangeView.setText(spell.range);
        areaOfEffectView.setText(spell.area_of_effect.equals("None") ? nothing : spell.area_of_effect);

        StringBuilder components = new StringBuilder(spell.components[0] + "");
        for (int i = 1; i < spell.components.length; i++)
            components.append(", ").append(spell.components[i]);

        componentsView.setText(components.toString());
        materialsView.setText(spell.materials.equals("None") ? nothing : spell.materials);
        castingTimeView.setText(spell.casting_time);
        attackTypeView.setText(spell.attack_type.equals("None") ? nothing : spell.attack_type);
        damageTypeView.setText(spell.damage_type);
        descriptionView.setText(spell.description);

        float f = getResources().getDimensionPixelSize(R.dimen.font_size_x);

        if (spell.damage_type.equals(nothing))
            ((LinearLayout) findViewById(R.id.damage_lvl_linear)).removeAllViews();
        else
            for (int i = 0; i < 10; i++)
            {
                if (spell.damage_lvl[i].equals(""))
                    continue;

                TextView textView = new TextView(this);

                String damageLevel =
                        getResources().getString(R.string.level) + " " + i + ": " + spell.damage_lvl[i];

                textView.setText(damageLevel);
                textView.setPadding(5, 5, 5, 5);
                textView.setTextSize(f);

                damageLevelLayout.addView(textView);
            }

        String[] classes = getResources().getStringArray(R.array.classes);

        for (int c: spell.classes)
        {
            TextView view = new TextView(this);

            view.setPadding(5, 5, 5, 5);
            view.setText(classes[c - 1]);
            view.setTextSize(f);

            classesLayout.addView(view);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spell);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        LoadSpell(Spell.fromBundle(bundle));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_spell, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}