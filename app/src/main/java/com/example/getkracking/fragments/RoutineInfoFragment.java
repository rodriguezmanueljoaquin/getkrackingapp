package com.example.getkracking.fragments;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.getkracking.HomeActivity;
import com.example.getkracking.R;
import com.example.getkracking.adapters.CyclesAdapter;
import com.example.getkracking.app.MyApplication;
import com.example.getkracking.entities.CycleVO;
import com.example.getkracking.entities.RoutineVO;
import com.example.getkracking.repository.RoutineRepository;
import com.example.getkracking.viewmodels.RepositoryViewModelFactory;
import com.example.getkracking.viewmodels.RoutineInfoViewModel;

import java.util.ArrayList;

public class RoutineInfoFragment extends Fragment {

    private RecyclerView cyclesRoutine;
    private ArrayList<CycleVO> cyclesList;
    private boolean favorited = false;  //HARDCODEADO OBTENIDO DE API
    private CyclesAdapter adapter;
    private RoutineInfoViewModel routineViewModel;
    private Switch mode;
    private ImageView favIcon;
    private RoutineVO routine;
    private int routineId;
    private NavController navController;

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        Toolbar mToolBar = ((HomeActivity) getActivity()).findViewById(R.id.homeTopBar);
        ActionBar actionBar = ((HomeActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.routine);

        //CUSTOMIZAR BACK BUTTON
        ((HomeActivity) getActivity()).setSupportActionBar(mToolBar);
        actionBar.setDisplayHomeAsUpEnabled(true);
        mToolBar.setNavigationIcon(R.drawable.ic_chevron_left);
        mToolBar.setNavigationOnClickListener(v -> Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.topbarmenu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //handle options selected
        int id = item.getItemId();
        if (id == R.id.topbar_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Mira esta rutina de ejercicio: " + routine.getName() + " de " + routine.getCreator() + "\n"
                    + "http://www.getkracking.com/routines/" + routineId);
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.routine_info_fragment, container, false);

        RepositoryViewModelFactory viewModelFactory = new RepositoryViewModelFactory(RoutineRepository.class, ((MyApplication) getActivity().getApplication()).getRoutineRepository());
        routineViewModel = new ViewModelProvider(this, viewModelFactory).get(RoutineInfoViewModel.class);


        mode = (Switch) vista.findViewById(R.id.execution_mode_chip);
        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        routineViewModel.getSwitchValue().observe(getViewLifecycleOwner(), bool -> {
            if (bool)
                mode.setChecked(true);
            else
                mode.setChecked(false);
        });

        mode.setOnClickListener(v -> routineViewModel.changeSwitchValue());

        if(getArguments() == null)
            navController.navigate(R.id.homeFragment);

        RoutineInfoFragmentArgs args = RoutineInfoFragmentArgs.fromBundle(getArguments());
        routineId = args.getIdRoutine();
        routineViewModel.getRoutineById(routineId).observe(getViewLifecycleOwner(),
                resource -> {
                    switch (resource.status) {
                        case LOADING:
                            Log.d("UI", "awaiting for routine");
                            break;
                        case SUCCESS:
                            Log.d("UI", "Éxito recuperando rutina");
                            routine = resource.data;
                            routineViewModel.getFavouriteRoutines().observe(getViewLifecycleOwner(), favresource -> {
                                        switch (favresource.status) {
                                            case LOADING:
                                                Log.d("UI", "awaiting favourite routines in infoRutina");
                                                break;
                                            case SUCCESS:
                                                Log.d("UI", "Éxito recuperando rutinas favoritas en inforutina");

                                                if (favresource.data.size() > 0) {
                                                    for (RoutineVO favRoutine : favresource.data) {
                                                        if (routine.getId() == favRoutine.getId()) {
                                                            Log.d("UI", "MATCH!");
                                                            routine.setFavorited(true);
                                                            fillRoutineData(vista);
                                                            //si dio que era favorito actualizo el display
                                                            break;
                                                        }
                                                    }
                                                }

                                                break;
                                            case ERROR:
                                                Log.d("UI", "Error recuperando rutinas favoritas en inforutina - " + favresource.message);
                                                break;
                                        }
                                    }
                            );
                            fillRoutineData(vista);
                            break;
                        case ERROR:
                            Log.d("UI", "Error recuperando la rutina " + routine.getId() + " - " + resource.message);
                            break;
                    }
                }
        );

        favIcon = vista.findViewById(R.id.favoriteIconInfoRoutine);

        favIcon.setOnClickListener((View.OnClickListener) v -> {
            if (favorited) {
                //SACAR DE FAVORITOS CON LA API
                removeFromFavourites(routine);
            } else {
                //AGREGAR A FAVORITOS CON LA API
                addToFavourites(routine);
            }
        });


        cyclesRoutine = vista.findViewById(R.id.CyclesRoutine);
        cyclesList = new ArrayList<>();

        adapter = new CyclesAdapter(getActivity(), cyclesList);
        cyclesRoutine.setLayoutManager(new LinearLayoutManager(getContext()));
        cyclesRoutine.setAdapter(adapter);
        cyclesRoutine.setNestedScrollingEnabled(false);
        fillCycles();

        return vista;
    }

    private void fillRoutineData(View vista) {
        ((TextView) vista.findViewById(R.id.RoutineNameInRoutine)).setText(routine.getName());
        ((TextView) vista.findViewById(R.id.CreatorNameInRoutine)).setText(routine.getCreator());
        ((TextView) vista.findViewById(R.id.RoutineDescriptionInRoutine)).setText(routine.getDescription());
        ((RatingBar) vista.findViewById(R.id.rbCategory1InRoutine)).setRating(routine.getDifficulty());
        favorited = routine.isFavorited();
        TextView puntuacion = vista.findViewById(R.id.rating_value_info_routine);
        puntuacion.setText(String.valueOf(routine.getRating()));


        if (favorited) {
            favIcon.setBackgroundResource(R.drawable.ic_favorite);
        } else {
            favIcon.setBackgroundResource(R.drawable.ic_favorite_border);
        }
    }

    private void addToFavourites(RoutineVO routine) {
        routineViewModel.addToFavourites(routine).observe(getViewLifecycleOwner(),
                resource -> {
                    switch (resource.status) {
                        case LOADING:
                            Log.d("UI", "awaiting favourites to add");
                            break;
                        case SUCCESS:
                            Log.d("UI", "Éxito agregando a favoritos");
                            favorited = true;
                            favIcon.setBackgroundResource(R.drawable.ic_favorite);
                            break;
                        case ERROR:
                            Log.d("UI", "Error agregando a favoritos - " + resource.message);
                            break;
                    }

                });
    }

    private void removeFromFavourites(RoutineVO routine) {
        routineViewModel.removeFromFavourites(routine).observe(getViewLifecycleOwner(),
                resource -> {
                    switch (resource.status) {
                        case LOADING:
                            Log.d("UI", "awaiting favourites to remove");
                            break;
                        case SUCCESS:
                            Log.d("UI", "Éxito quitando de favoritos");
                            favorited = false;
                            favIcon.setBackgroundResource(R.drawable.ic_favorite_border);
                            break;
                        case ERROR:
                            Log.d("UI", "Error quitando de favoritos - " + resource.message);
                            break;
                    }

                });
    }

    private void fillExercises(int cycleId, CycleVO cycle) {
        routineViewModel.getExercises(routineId, cycleId).observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    Log.d("UI", "awaiting routines");
                    break;
                case SUCCESS:
                    Log.d("UI", "Éxito recuperando rutinas");

                    if (resource.data != null) {
                        cycle.getExercises().addAll(resource.data);
                        adapter.notifyDataSetChanged();

                        if (cyclesList.indexOf(cycle) == cyclesList.size() - 1)
                            //seteo de funcionalidades de botones
                            ((Button) getView().findViewById(R.id.ButtonEmpezarInRoutine)).setOnClickListener(v1 -> {
                                if (mode.isChecked()) {
                                    RoutineInfoFragmentDirections.ActionRoutineInfoFragmentToRunRoutineListFragment action =
                                            RoutineInfoFragmentDirections.actionRoutineInfoFragmentToRunRoutineListFragment(cyclesList.toArray(new CycleVO[cyclesList.size()]), routine.getId());
                                    navController.navigate(action);
                                } else {
                                    RoutineInfoFragmentDirections.ActionRoutineInfoFragmentToRunRoutineFragment action =
                                            RoutineInfoFragmentDirections.actionRoutineInfoFragmentToRunRoutineFragment(cyclesList.toArray(new CycleVO[cyclesList.size()]), routine.getId());
                                    navController.navigate(action);
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

    private void fillCycles() {
        routineViewModel.getCycles(routineId).observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    Log.d("UI", "awaiting routines");
                    break;
                case SUCCESS:
                    Log.d("UI", "Éxito recuperando rutinas");

                    if (resource.data != null)
                        for (CycleVO cycle : resource.data) {
                            cyclesList.add(cycle);
                            fillExercises(cycle.getId(), cycle);
                            adapter.notifyDataSetChanged();
                        }

                    break;
                case ERROR:
                    Log.d("UI", "Error en get routines - " + resource.message);
                    break;
            }
        });
    }
}