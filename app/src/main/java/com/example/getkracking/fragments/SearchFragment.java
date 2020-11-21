package com.example.getkracking.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.getkracking.HomeActivity;
import com.example.getkracking.R;
import com.example.getkracking.adapters.RoutinesAdapter;
import com.example.getkracking.app.MyApplication;
import com.example.getkracking.entities.RoutineVO;
import com.example.getkracking.repository.RoutineRepository;
import com.example.getkracking.viewmodels.RepositoryViewModelFactory;
import com.example.getkracking.viewmodels.RoutinesViewModel;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class SearchFragment extends Fragment implements RoutinesAdapter.OnRoutineListener {
    RecyclerView recyclerRoutines;
    ArrayList<RoutineVO> routinesList;
    RoutinesViewModel routinesViewModel;
    RoutinesAdapter adapter;
    AtomicBoolean zonaCritica = new AtomicBoolean(true);
    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        if (((HomeActivity) getActivity()).getSupportActionBar() != null)
            ((HomeActivity) getActivity()).getSupportActionBar().setTitle(R.string.bottombaricon_search);

        super.onResume();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_search, container, false);

        EditText etSearch = vista.findViewById(R.id.etSearchRoutines);
        etSearch.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    fillListSearch(etSearch.getText().toString());
                    return true;
            }
            return false;
        });

        //chip de filtros
        ChipGroup filters = vista.findViewById(R.id.chipgroup_filterSearch);
        filters.setOnCheckedChangeListener((group, id) -> {
            if (id == R.id.filterchip_favourites) {
                //completar
            } else if (id == R.id.filterchip_highdifficulty) {
                //completar
            } else if (id == R.id.filterchip_mediumdifficulty) {
                //completar
            } else if (id == R.id.filterchip_lowdifficulty) {
                //completar
            }
        });

        recyclerRoutines = vista.findViewById(R.id.recyclerSearchRoutines);
        recyclerRoutines.setLayoutManager(new LinearLayoutManager(getContext()));

        RepositoryViewModelFactory viewModelFactory = new RepositoryViewModelFactory(RoutineRepository.class, ((MyApplication) getActivity().getApplication()).getRoutineRepository());
        routinesViewModel = new ViewModelProvider(this, viewModelFactory).get(RoutinesViewModel.class);

        //lista rutinas
        routinesList = new ArrayList<>();
        adapter = new RoutinesAdapter(routinesList, this, null);
        recyclerRoutines.setAdapter(adapter);
        recyclerRoutines.setNestedScrollingEnabled(false);

        fillList();
        return vista;
    }

    @Override
    public void onRoutineClick(int position, String type) {
        SearchFragmentDirections.ActionSearchFragmentToRoutineInfoFragment action = SearchFragmentDirections.actionSearchFragmentToRoutineInfoFragment
                (routinesList.get(position).getId());

        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
    }

    @Override
    public void onStop() {
        routinesViewModel.getFavouriteRoutines().removeObservers(getViewLifecycleOwner());
        routinesViewModel.getRoutines().removeObservers(getViewLifecycleOwner());
        super.onStop();
    }

    private void fillListSearch(String search) {
        if( search.length() < 3 ){
            Toast.makeText(getContext() , "mínimo 3 carácteres" , Toast.LENGTH_SHORT);
            fillList();
            return;
        }
        routinesList.clear();
        adapter.notifyDataSetChanged();
        if (!routinesViewModel.getRoutines().hasActiveObservers()) {
            routinesViewModel.searchRoutines(search).observe(getViewLifecycleOwner(), resource -> {
                switch (resource.status) {
                    case LOADING:
                        Log.d("UI", "awaiting routines de search");
                        break;
                    case SUCCESS:
                        Log.d("UI", "Éxito recuperando rutinas de search");

                        if (resource.data != null)
                            routinesList.addAll(resource.data);

                        if (!routinesViewModel.getFavouriteRoutines().hasActiveObservers()) {
                            routinesViewModel.getFavouriteRoutines().observe(getViewLifecycleOwner(), favresource -> {
                                switch (favresource.status) {
                                    case LOADING:
                                        Log.d("UI", "awaiting favourite routines de search");
                                        break;
                                    case SUCCESS:
                                        Log.d("UI", "Éxito recuperando rutinas favoritas de search");

                                        if (favresource.data != null && favresource.data.size() > 0) {
                                            for (RoutineVO routine : routinesList) {
                                                for (RoutineVO favRoutine : favresource.data) {
                                                    if (routine.getId() == favRoutine.getId()) {
                                                        Log.d("UI", "MATCH!");
                                                        routine.setFavorited(true);
                                                        break;
                                                    }
                                                }
                                            }
                                        }

                                        adapter.notifyDataSetChanged();

                                        break;
                                    case ERROR:
                                        Log.d("UI", "Error recuperando rutinas favoritas de search - " + favresource.message);
                                        break;
                                }
                            });
                        }

                        adapter.notifyDataSetChanged();

                        break;
                    case ERROR:
                        Log.d("UI", "Error en get routines de search - " + resource.message);
                        break;
                }
            });
        }
    }

    private void fillList() {
        if (!routinesViewModel.getRoutines().hasActiveObservers()) {
            routinesViewModel.getRoutines().observe(getViewLifecycleOwner(), resource -> {
                switch (resource.status) {
                    case LOADING:
                        Log.d("UI", "awaiting routines");
                        break;
                    case SUCCESS:
                        Log.d("UI", "Éxito recuperando rutinas");
                        if (resource.data != null)
                            routinesList.addAll(resource.data);

                        if (!routinesViewModel.getFavouriteRoutines().hasActiveObservers()) {
                            routinesViewModel.getFavouriteRoutines().observe(getViewLifecycleOwner(), favresource -> {
                                switch (favresource.status) {
                                    case LOADING:
                                        Log.d("UI", "awaiting favourite routines");
                                        break;
                                    case SUCCESS:
                                        Log.d("UI", "Éxito recuperando rutinas favoritas");

                                        if (favresource.data != null && favresource.data.size() > 0) {
                                            for (RoutineVO routine : routinesList) {
                                                for (RoutineVO favRoutine : favresource.data) {
                                                    if (routine.getId() == favRoutine.getId()) {
                                                        Log.d("UI", "MATCH!");
                                                        routine.setFavorited(true);
                                                        break;
                                                    }
                                                }
                                            }
                                        }

                                        adapter.notifyDataSetChanged();

                                        break;
                                    case ERROR:
                                        Log.d("UI", "Error recuperando rutinas favoritas - " + favresource.message);
                                        break;
                                }
                            });
                        }
                        break;
                    case ERROR:
                        Log.d("UI", "Error en get routines - " + resource.message);
                        break;
                }
            });
        }
    }

}