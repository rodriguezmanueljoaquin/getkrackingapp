package com.example.getkracking.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.getkracking.HomeActivity;
import com.example.getkracking.R;
import com.example.getkracking.WelcomeActivity;
import com.example.getkracking.app.MyApplication;
import com.example.getkracking.repository.UserRepository;
import com.example.getkracking.viewmodels.RepositoryViewModelFactory;
import com.example.getkracking.viewmodels.UserViewModel;

import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;


public class PerfilFragment extends Fragment {

    private UserViewModel userViewModel;
    private TextView tvName;

    private String imageUrl, fullName, userName, email;
    private int userId;

    public PerfilFragment() {
        // Required empty public constructor
    }


    @Override
    public void onResume() {
        if (((HomeActivity) getActivity()).getSupportActionBar() != null)
            ((HomeActivity) getActivity()).getSupportActionBar().setTitle(R.string.bottombaricon_perfil);

        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_perfil, container, false);
        tvName = vista.findViewById(R.id.name_perfil);

        RepositoryViewModelFactory viewModelFactory = new RepositoryViewModelFactory(UserRepository.class, ((MyApplication) getActivity().getApplication()).getUserRepository());
        userViewModel = new ViewModelProvider(this, viewModelFactory).get(UserViewModel.class);

        fetchUserData();

        userViewModel.getImageBM().observe(getViewLifecycleOwner(),
                bitmap -> ((CircleImageView) vista.findViewById(R.id.picture_perfil)).setImageBitmap(bitmap));

        LinearLayoutCompat editPerfil = vista.findViewById(R.id.EditProfileCompat);
        editPerfil.setOnClickListener(v1 -> {
            PerfilFragmentDirections.ActionPerfilFragmentToEditPerfilFragment action = PerfilFragmentDirections.actionPerfilFragmentToEditPerfilFragment(
                    tvName.getText().toString(),
                    userName,
                    email,
                    imageUrl,
                    userId
            );

            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
        });

        LinearLayoutCompat logout = vista.findViewById(R.id.LogoutCompat);

        logout.setOnClickListener(v -> {
            performLogout();
        });

        return vista;
    }

    private void fetchUserData(){

        userViewModel.getCurrent().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    Log.d("UI", "awaiting user data");
                    break;
                case SUCCESS:
                    Log.d("UI", "Éxito recuperando datos");

                    fullName = resource.data.getFullName();
                    tvName.setText(fullName);
                    userName = resource.data.getUsername();
                    imageUrl = resource.data.getAvatarUrl();
                    email = resource.data.getEmail();
                    userId = resource.data.getId();

                    Thread loadImage = new Thread(() -> {
                        try {
                            URL newurl = new URL(imageUrl);
                            Bitmap bm = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                            ((Activity) getContext()).runOnUiThread(() -> userViewModel.setImageBM(bm));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    loadImage.start();
                    break;
                case ERROR:
                    Log.d("UI", "Error en get edición de perfil - " + resource.message);
                    break;
            }
        });
    }

    private void performLogout(){
        userViewModel.logout().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    Log.d("UI", "awaiting");
                    break;
                case SUCCESS:
                    Log.d("UI", "Cierre de sesión exitoso" );
                    MyApplication app = (MyApplication) getActivity().getApplication();
                    app.getPreferences().setAuthToken(null);
                    app.getPreferences().setPassword(null);
                    app.getPreferences().setUsername(null);
                    app.getPreferences().setLogged(false);
                    Intent welcomeIntent = new Intent(getActivity(), WelcomeActivity.class);
                    startActivity(welcomeIntent);
                    getActivity().finish();
                    break;
                case ERROR:
                    Log.d("UI", "Error al cerrar sesión");
                    break;
            }
        });
    }

}