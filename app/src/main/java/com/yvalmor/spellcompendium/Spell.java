package com.yvalmor.spellcompendium;

import android.os.Bundle;
import android.util.Log;

import org.json.*;

import java.util.Iterator;

public class Spell {
    public String name;
    public String description;
    public String range;
    public char[] components;
    public String materials;
    public boolean ritual;
    public String area_of_effect;
    public String duration;
    public boolean concentration;
    public String casting_time;
    public int level;
    public String attack_type;
    public String damage_type;
    public String[] damage_lvl;
    public String school;
    public int[] classes;

    public Spell(String name, String description, String range, char[] components, String materials,
                 boolean ritual, String area_of_effect, String duration, boolean concentration,
                 String casting_time, int level, String attack_type, String damage_type,
                 String[] damage_lvl, String school, int[] classes) {
        this.name = name;
        this.description = description;
        this.range = range;
        this.components = components;
        this.materials = materials;
        this.ritual = ritual;
        this.area_of_effect = area_of_effect;
        this.duration = duration;
        this.concentration = concentration;
        this.casting_time = casting_time;
        this.level = level;
        this.attack_type = attack_type;
        this.damage_type = damage_type;
        this.damage_lvl = damage_lvl;
        this.school = school;
        this.classes = classes;
    }

    public static Spell fromJson(JSONObject jObject) {
        try {
            JSONArray jsonComponents = jObject.getJSONArray("components");
            char[] components = new char[jsonComponents.length()];

            for (int i = 0; i < components.length; i++)
                components[i] = jsonComponents.getString(i).charAt(0);


            String area;
            if (jObject.isNull("area_of_effect"))
                area = "None";
            else {
                JSONObject jsonAreaOfEffects = jObject.getJSONObject("area_of_effect");
                area = jsonAreaOfEffects.getString("type") + ": " +
                                jsonAreaOfEffects.getString("size") + "m";
            }


            JSONObject jsonDamageLvl = jObject.getJSONObject("damage_lvl");
            String[] damage_lvl = new String[10];

            for (int i = 0; i < 10; i++)
                damage_lvl[i] = "";

            for (Iterator<String> it = jsonDamageLvl.keys(); it.hasNext(); ) {
                String key = it.next();
                damage_lvl[Integer.parseInt(key)] = jsonDamageLvl.getString(key);
            }


            JSONArray jsonClasses = jObject.getJSONArray("classes");
            int[] classes = new int[jsonClasses.length()];

            for (int i = 0; i < classes.length; i++)
                classes[i] = jsonClasses.getInt(i);


            return new Spell(
                    jObject.getString("name"),
                    jObject.getString("description"),
                    jObject.getString("range"),
                    components,
                    jObject.getString("materials"),
                    jObject.getBoolean("ritual"),
                    area,
                    jObject.getString("duration"),
                    jObject.getBoolean("concentration"),
                    jObject.getString("casting_time"),
                    jObject.getInt("level"),
                    jObject.getString("attack_type"),
                    jObject.getString("damage_type"),
                    damage_lvl,
                    jObject.getString("school"),
                    classes
            );
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                Log.println(Log.ERROR, "Error", jObject.getString("name"));
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
            }
            return null;
        }
    }

    public static Spell fromBundle(Bundle bundle) {
        return new Spell(
                bundle.getString("name"),
                bundle.getString("description"),
                bundle.getString("range"),
                bundle.getCharArray("components"),
                bundle.getString("materials"),
                bundle.getBoolean("ritual"),
                bundle.getString("area_of_effect"),
                bundle.getString("duration"),
                bundle.getBoolean("concentration"),
                bundle.getString("casting_time"),
                bundle.getInt("level"),
                bundle.getString("attack_type"),
                bundle.getString("damage_type"),
                bundle.getStringArray("damage_lvl"),
                bundle.getString("school"),
                bundle.getIntArray("classes")
        );
    }

    public static Bundle getBundle(Spell spell) {
        Bundle bundle = new Bundle();

        bundle.putString("name", spell.name);
        bundle.putString("description", spell.description);
        bundle.putString("range", spell.range);
        bundle.putCharArray("components", spell.components);
        bundle.putString("materials", spell.materials);
        bundle.putBoolean("ritual", spell.ritual);
        bundle.putString("area_of_effect", spell.area_of_effect);
        bundle.putString("duration", spell.duration);
        bundle.putBoolean("concentration", spell.concentration);
        bundle.putString("casting_time", spell.casting_time);
        bundle.putInt("level", spell.level);
        bundle.putString("attack_type", spell.attack_type);
        bundle.putString("damage_type", spell.damage_type);
        bundle.putStringArray("damage_lvl", spell.damage_lvl);
        bundle.putString("school", spell.school);
        bundle.putIntArray("classes", spell.classes);

        return bundle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public char[] getComponents() {
        return components;
    }

    public void setComponents(char[] components) {
        this.components = components;
    }

    public String getMaterials() {
        return materials;
    }

    public void setMaterials(String materials) {
        this.materials = materials;
    }

    public boolean isRitual() {
        return ritual;
    }

    public void setRitual(boolean ritual) {
        this.ritual = ritual;
    }

    public String getArea_of_effect() {
        return area_of_effect;
    }

    public void setArea_of_effect(String area_of_effect) {
        this.area_of_effect = area_of_effect;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean isConcentration() {
        return concentration;
    }

    public void setConcentration(boolean concentration) {
        this.concentration = concentration;
    }

    public String getCasting_time() {
        return casting_time;
    }

    public void setCasting_time(String casting_time) {
        this.casting_time = casting_time;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getAttack_type() {
        return attack_type;
    }

    public void setAttack_type(String attack_type) {
        this.attack_type = attack_type;
    }

    public String getDamage_type() {
        return damage_type;
    }

    public void setDamage_type(String damage_type) {
        this.damage_type = damage_type;
    }

    public String[] getDamage_lvl() {
        return damage_lvl;
    }

    public void setDamage_lvl(String[] damage_lvl) {
        this.damage_lvl = damage_lvl;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public int[] getClasses() {
        return classes;
    }

    public void setClasses(int[] classes) {
        this.classes = classes;
    }
}
