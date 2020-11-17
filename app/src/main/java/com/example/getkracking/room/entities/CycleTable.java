package com.example.getkracking.room.entities;

import androidx.recyclerview.widget.SortedList;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "cycleTable")
public class CycleTable {
    private List<ExerciseTable> exercises;
    @PrimaryKey
    public int cycleId;

    @ForeignKey(entity = RoutineTable.class , parentColumns = "id" , childColumns = "RoutineId")
    public int routineId;

    @ColumnInfo(name = "Detail")
    public String detail;

    @ColumnInfo(name = "Order")
    public int order;

    public void setExercises( List<ExerciseTable> exercises){
        this.exercises =  exercises;
    }

    public List<ExerciseTable> getExercises(){
        return exercises;
    }

}
