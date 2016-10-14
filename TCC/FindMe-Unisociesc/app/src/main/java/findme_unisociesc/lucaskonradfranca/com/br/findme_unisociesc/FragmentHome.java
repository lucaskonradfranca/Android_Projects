package findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentHome extends Fragment {

    private TextView textoLocalizacao;
    private ImageView imagemLocalizacao;
    private Button botaoLocalizar;
    private Button botaoAtualizar;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textoLocalizacao = (TextView) view.findViewById(R.id.idTxtLocalizacao);
        imagemLocalizacao = (ImageView) view.findViewById(R.id.idImagemLocalizacao);
        botaoLocalizar = (Button) view.findViewById(R.id.idBotaoLocalizar);
        botaoAtualizar = (Button) view.findViewById(R.id.idBotaoAtualizar);

        botaoLocalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"Localizar um amigo",Toast.LENGTH_SHORT).show();
            }
        });

        botaoAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"Atualizar minha localização",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_layout, container, false);
        return v;
    }
}
