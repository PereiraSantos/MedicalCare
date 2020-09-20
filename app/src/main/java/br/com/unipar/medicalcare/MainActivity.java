package br.com.unipar.medicalcare;

import androidx.appcompat.app.AppCompatActivity;
import br.com.unipar.medicalcare.dao.PacienteDAO;
import br.com.unipar.medicalcare.database.AppDatabase;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

import java.lang.ref.WeakReference;


import br.com.unipar.medicalcare.database.RoomDatabaseOpenHelper;
import br.com.unipar.medicalcare.entities.Paciente;

public class MainActivity extends AppCompatActivity {

    private String textoPadrao = "deve ser informado";
    private EditText nome, horario, medico, celular;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // formata campos.
        celular = findViewById(R.id.edCelular);
        celular.addTextChangedListener(new MaskTextWatcher(celular, new SimpleMaskFormatter("(NN)NNNNN-NNNN")));
        horario = findViewById(R.id.edHorario);
        horario.addTextChangedListener(new MaskTextWatcher(horario, new SimpleMaskFormatter("NN/NN/NNNN--NN:NN")));
    }

    // classe que validar os campo da tela.
    public void  marcar(View view){

        nome = findViewById(R.id.edNome);
        medico = findViewById(R.id.edMedico);

        if(nome.getText().toString().equals("")) mensagem("Nome "+ textoPadrao);
        else if(horario.getText().toString().equals("")) mensagem("Horario "+ textoPadrao);
        else if(medico.getText().toString().equals("")) mensagem("Medico "+ textoPadrao);
        else if(celular.getText().toString().equals("")) mensagem("Celular "+ textoPadrao);
        else{
            salvaPacinete(nome.getText().toString(), horario.getText().toString(),
                          medico.getText().toString(), celular.getText().toString());
        }
    }

    // chama segunda tela
    public void  consultar(View view){
        segundaTela(view);
    }

    // criando objeto paciente
    public void salvaPacinete(String nome, String horario, String medico, String celular){
        Paciente paciente = new Paciente();
        paciente.setNome(nome);
        paciente.setHoraAgendamento(horario);
        paciente.setNomeMedico(medico);
        paciente.setCelular(celular);

        salvarPacienteThread(paciente);
    }

    // salvar registro vindo da tela main.
    // criando thread para não trava tela usuario.
    public void salvarPacienteThread(Paciente paciente) {
        AppDatabase appDatabase = RoomDatabaseOpenHelper.getDatabase(new WeakReference<Context>(this));
        new Thread(new Runnable() {
            public void run() {
                PacienteDAO pacienteDao = appDatabase.pacienteDAO();
                pacienteDao.insertAll(paciente);
            }
        }).start();
        mensagem("Cadastro Realizado.");
        limparFormulario();
    }

    // Classe prepara para ir para segunda tela.
    public void segundaTela(View view){
        Intent intent = new Intent(this, ListaFiltraPaciente.class);
        Bundle b = new Bundle();
        intent.putExtras(b);
        startActivity(intent);
    }

    // limpar formulario pra evitar lixo.
    public void limparFormulario(){
        nome.setText("");
        horario.setText("");
        medico.setText("");
        celular.setText("");
    }

    public void mensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
    }

}
