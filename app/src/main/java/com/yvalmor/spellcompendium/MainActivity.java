package com.yvalmor.spellcompendium;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchView searchView;

    private Menu menu;

    private SpellSortedListAdapter adapter;

    private List<Spell> spells;

    private SharedPreferences sharedPreferences;

    private String searchFilter = "None",
            schoolFilter = "None";
    private int levelFilter = -1,
            schoolFilterIndex = -1;

    private int[] menuItemsId = new int[]{
            R.id.filter_level_0,
            R.id.filter_level_1,
            R.id.filter_level_2,
            R.id.filter_level_3,
            R.id.filter_level_4,
            R.id.filter_level_5,
            R.id.filter_level_6,
            R.id.filter_level_7,
            R.id.filter_level_8,
            R.id.filter_level_9,
            R.id.filter_school_0,
            R.id.filter_school_1,
            R.id.filter_school_2,
            R.id.filter_school_3,
            R.id.filter_school_4,
            R.id.filter_school_5,
            R.id.filter_school_6,
            R.id.filter_school_7,
    };

    private void LoadSpells() {
        InputStream inputStream = getResources().openRawResource(R.raw.spells);

        Writer writer = new StringWriter();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream)
            );

            String line = reader.readLine();
            while (line != null) {
                writer.write(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        spells = new ArrayList<Spell>();

        try {
            JSONObject jObject = new JSONObject(writer.toString());

            JSONArray jSpells = jObject.getJSONArray("spells");

            int size = jObject.getInt("count");

            for (int i = 0; i < size; i++) {
                JSONObject obj = jSpells.getJSONObject(i);

                Spell spell = Spell.fromJson(obj);
                assert spell != null;

                spells.add(spell);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getApplicationContext()
                .getSharedPreferences("filter_prefs", Context.MODE_PRIVATE);

        String lang = sharedPreferences.getString("lang", "fr");

        Locale locale = lang.equals("fr") ? Locale.FRENCH : Locale.ENGLISH;

        Configuration config = getResources().getConfiguration();

        config.setLocale(locale);
        Locale.setDefault(locale);

        getResources().updateConfiguration(config,
                getResources().getDisplayMetrics());

        LoadSpells();

        recyclerView = (RecyclerView) this.findViewById(R.id.spellRecyclerView);
        adapter = new SpellSortedListAdapter(this, spells);

        recyclerView.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        levelFilter = sharedPreferences.getInt("filter_level", -1);
        schoolFilterIndex = sharedPreferences.getInt("filter_school", -1);

        updateSchoolFilterValue();

        adapter.replaceAll(filter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;

        getMenuInflater().inflate(R.menu.app_bar_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterByQuery(query);
                adapter.replaceAll(filter());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterByQuery(newText);
                adapter.replaceAll(filter());
                return false;
            }
        });

        int searchCloseBtnId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeBtn = searchView.findViewById(searchCloseBtnId);
        closeBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (searchView.getQuery().toString().trim().equals("")) {
                            searchView.setIconified(true);
                            return;
                        }
                        searchView.setQuery("", false);
                        sharedPreferences.edit().putString("filter_query", "None").apply();
                        searchFilter = "None";
                        adapter.replaceAll(filter());
                        searchView.setIconified(true);
                    }
                }
        );

        if (levelFilter != -1) {
            MenuItem item = menu.findItem(menuItemsId[levelFilter]);
            item.setCheckable(true);
            item.setChecked(true);
        }

        if (schoolFilterIndex != -1) {
            MenuItem item = menu.findItem(menuItemsId[schoolFilterIndex + 10]);
            item.setCheckable(true);
            item.setChecked(true);
        }

        if (!searchFilter.equals("None"))
            searchView.setQuery(searchFilter, false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, 1);
                return true;
            case R.id.action_search:
                return true;

            case R.id.sort_name:
                sortByName();
                return true;
            case R.id.sort_level:
                sortByLevel();
                return true;
            case R.id.sort_school:
                sortBySchool();
                return true;

            case R.id.filter_level_0:
                uncheckLevelItems(0);
                if (item.isChecked()) {
                    filterByLevel(-1);
                    item.setChecked(false);
                    item.setCheckable(false);
                    return true;
                }
                item.setChecked(true);
                item.setCheckable(true);
                filterByLevel(0);
                return true;
            case R.id.filter_level_1:
                uncheckLevelItems(1);
                if (item.isChecked()) {
                    filterByLevel(-1);
                    item.setChecked(false);
                    item.setCheckable(false);
                    return true;
                }
                item.setChecked(true);
                item.setCheckable(true);
                filterByLevel(1);
                return true;
            case R.id.filter_level_2:
                uncheckLevelItems(2);
                if (item.isChecked()) {
                    filterByLevel(-1);
                    item.setChecked(false);
                    item.setCheckable(false);
                    return true;
                }
                item.setChecked(true);
                item.setCheckable(true);
                filterByLevel(2);
                return true;
            case R.id.filter_level_3:
                uncheckLevelItems(3);
                if (item.isChecked()) {
                    filterByLevel(-1);
                    item.setChecked(false);
                    item.setCheckable(false);
                    return true;
                }
                item.setChecked(true);
                item.setCheckable(true);
                filterByLevel(3);
                return true;
            case R.id.filter_level_4:
                uncheckLevelItems(4);
                if (item.isChecked()) {
                    filterByLevel(-1);
                    item.setChecked(false);
                    item.setCheckable(false);
                    return true;
                }
                item.setChecked(true);
                item.setCheckable(true);
                filterByLevel(4);
                return true;
            case R.id.filter_level_5:
                uncheckLevelItems(5);
                if (item.isChecked()) {
                    filterByLevel(-1);
                    item.setChecked(false);
                    item.setCheckable(false);
                    return true;
                }
                item.setChecked(true);
                item.setCheckable(true);
                filterByLevel(5);
                return true;
            case R.id.filter_level_6:
                uncheckLevelItems(6);
                if (item.isChecked()) {
                    filterByLevel(-1);
                    item.setChecked(false);
                    item.setCheckable(false);
                    return true;
                }
                item.setChecked(true);
                item.setCheckable(true);
                filterByLevel(6);
                return true;
            case R.id.filter_level_7:
                uncheckLevelItems(7);
                if (item.isChecked()) {
                    filterByLevel(-1);
                    item.setChecked(false);
                    item.setCheckable(false);
                    return true;
                }
                item.setChecked(true);
                item.setCheckable(true);
                filterByLevel(7);
                return true;
            case R.id.filter_level_8:
                uncheckLevelItems(8);
                if (item.isChecked()) {
                    filterByLevel(-1);
                    item.setChecked(false);
                    item.setCheckable(false);
                    return true;
                }
                item.setChecked(true);
                item.setCheckable(true);
                filterByLevel(8);
                return true;
            case R.id.filter_level_9:
                uncheckLevelItems(9);
                if (item.isChecked()) {
                    filterByLevel(-1);
                    item.setChecked(false);
                    item.setCheckable(false);
                    return true;
                }
                item.setChecked(true);
                item.setCheckable(true);
                filterByLevel(9);
                return true;

            case R.id.filter_school_0:
                uncheckSchoolItems(0);
                if (item.isChecked()) {
                    filterBySchool(-1);
                    item.setChecked(false);
                    item.setCheckable(false);
                    return true;
                }
                item.setChecked(true);
                item.setCheckable(true);
                filterBySchool(0);
                return true;
            case R.id.filter_school_1:
                uncheckSchoolItems(1);
                if (item.isChecked()) {
                    filterBySchool(-1);
                    item.setChecked(false);
                    item.setCheckable(false);
                    return true;
                }
                item.setChecked(true);
                item.setCheckable(true);
                filterBySchool(1);
                return true;
            case R.id.filter_school_2:
                uncheckSchoolItems(2);
                if (item.isChecked()) {
                    filterBySchool(-1);
                    item.setChecked(false);
                    item.setCheckable(false);
                    return true;
                }
                item.setChecked(true);
                item.setCheckable(true);
                filterBySchool(2);
                return true;
            case R.id.filter_school_3:
                uncheckSchoolItems(3);
                if (item.isChecked()) {
                    filterBySchool(-1);
                    item.setChecked(false);
                    item.setCheckable(false);
                    return true;
                }
                item.setChecked(true);
                item.setCheckable(true);
                filterBySchool(3);
                return true;
            case R.id.filter_school_4:
                uncheckSchoolItems(4);
                if (item.isChecked()) {
                    filterBySchool(-1);
                    item.setChecked(false);
                    item.setCheckable(false);
                    return true;
                }
                item.setChecked(true);
                item.setCheckable(true);
                filterBySchool(4);
                return true;
            case R.id.filter_school_5:
                uncheckSchoolItems(5);
                if (item.isChecked()) {
                    filterBySchool(-1);
                    item.setChecked(false);
                    item.setCheckable(false);
                    return true;
                }
                item.setChecked(true);
                item.setCheckable(true);
                filterBySchool(5);
                return true;
            case R.id.filter_school_6:
                uncheckSchoolItems(6);
                if (item.isChecked()) {
                    filterBySchool(-1);
                    item.setChecked(false);
                    item.setCheckable(false);
                    return true;
                }
                item.setChecked(true);
                item.setCheckable(true);
                filterBySchool(6);
                return true;
            case R.id.filter_school_7:
                uncheckSchoolItems(7);
                if (item.isChecked()) {
                    filterBySchool(-1);
                    item.setChecked(false);
                    item.setCheckable(false);
                    return true;
                }
                item.setChecked(true);
                item.setCheckable(true);
                filterBySchool(7);
                return true;

            case R.id.reset_filter:
                resetFilter();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void uncheckLevelItems(int index) {
        for (int i = 0; i < 10; i++) {
            if (i == index)
                continue;
            MenuItem item = menu.findItem(menuItemsId[i]);
            item.setChecked(false);
            item.setCheckable(false);
        }
    }

    private void uncheckSchoolItems(int index) {
        for (int i = 10; i < menuItemsId.length; i++) {
            if (i == 10 + index)
                continue;
            MenuItem item = menu.findItem(menuItemsId[i]);
            item.setChecked(false);
            item.setCheckable(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LoadSpells();
        recyclerView.swapAdapter(new SpellSortedListAdapter(this, spells),
                true);

        levelFilter = sharedPreferences.getInt("filter_level", -1);
        schoolFilterIndex = sharedPreferences.getInt("filter_school", -1);
        searchFilter = sharedPreferences.getString("filter_query", "None");

        if (schoolFilterIndex != -1)
            updateSchoolFilterValue();

        adapter.replaceAll(filter());
    }


    public void sortByName() {
        adapter.replaceAll(filter(), true, "NAME");
    }

    public void sortByLevel() {
        adapter.replaceAll(filter(), true, "LEVEL");
    }

    public void sortBySchool() {
        adapter.replaceAll(filter(), true, "SCHOOL");
    }


    private void filterByQuery(String query) {
        sharedPreferences.edit().putString("filter_query", query.trim()).apply();

        searchFilter = query.toLowerCase().trim();

        adapter.replaceAll(filter());
    }

    public void filterByLevel(int level) {
        sharedPreferences.edit().putInt("filter_level", level).apply();

        levelFilter = level;

        adapter.replaceAll(filter());
    }

    public void filterBySchool(int school) {
        sharedPreferences.edit().putInt("filter_school", school).apply();
        schoolFilterIndex = school;

        updateSchoolFilterValue();

        adapter.replaceAll(filter());
    }

    public List<Spell> filter() {
        List<Spell> spellList = new ArrayList<Spell>();
        int l = spells.size();
        Spell spell;

        if (!searchFilter.equals("None")) {
            if (levelFilter > -1) {
                if (!schoolFilter.equals("None")) {
                    for (int i = 0; i < l; i++) {
                        spell = spells.get(i);
                        if (spell.name.toLowerCase().startsWith(searchFilter) &&
                                spell.school.equalsIgnoreCase(schoolFilter) &&
                                spell.level == levelFilter)
                            spellList.add(spell);
                    }
                } else {
                    for (int i = 0; i < l; i++) {
                        spell = spells.get(i);
                        if (spell.name.toLowerCase().startsWith(searchFilter) &&
                                spell.level == levelFilter)
                            spellList.add(spell);
                    }
                }
            } else if (!schoolFilter.equals("None")) {
                for (int i = 0; i < l; i++) {
                    spell = spells.get(i);
                    if (spell.name.toLowerCase().startsWith(searchFilter) &&
                            spell.school.equalsIgnoreCase(schoolFilter))
                        spellList.add(spell);
                }
            } else {
                for (int i = 0; i < l; i++) {
                    spell = spells.get(i);
                    if (spell.name.toLowerCase().startsWith(searchFilter))
                        spellList.add(spell);
                }
            }
        } else if (levelFilter > -1) {
            if (!schoolFilter.equals("None")) {
                for (int i = 0; i < l; i++) {
                    spell = spells.get(i);
                    if (spell.school.equalsIgnoreCase(schoolFilter) &&
                            spell.level == levelFilter)
                        spellList.add(spell);
                }
            } else {
                for (int i = 0; i < l; i++) {
                    spell = spells.get(i);
                    if (spell.level == levelFilter)
                        spellList.add(spell);
                }
            }
        } else if (!schoolFilter.equals("None")) {
            for (int i = 0; i < l; i++) {
                spell = spells.get(i);
                if (spell.school.equalsIgnoreCase(schoolFilter))
                    spellList.add(spell);
            }
        } else {
            spellList = spells;
        }

        return spellList;
    }

    public void resetFilter() {
        searchFilter = "None";
        schoolFilter = "None";
        levelFilter = -1;

        searchView.clearFocus();
        searchView.setQuery("", false);

        for (int id : menuItemsId) {
            MenuItem item = menu.findItem(id);
            item.setChecked(false);
            item.setCheckable(false);
        }

        sharedPreferences.edit().putInt("filter_level", -1)
                .putString("filter_school", "None")
                .putString("filter_query", "None")
                .apply();

        adapter.replaceAll(spells);
    }

    public void updateSchoolFilterValue() {
        Resources r = getResources();

        if (schoolFilterIndex == -1) {
            schoolFilter = "None";
            return;
        }

        schoolFilter = new String[]{
                r.getString(R.string.school_0),
                r.getString(R.string.school_1),
                r.getString(R.string.school_2),
                r.getString(R.string.school_3),
                r.getString(R.string.school_4),
                r.getString(R.string.school_5),
                r.getString(R.string.school_6),
                r.getString(R.string.school_7)
        }[schoolFilterIndex];
    }
}