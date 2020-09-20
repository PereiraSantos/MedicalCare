package br.com.unipar.medicalcare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


import br.com.unipar.medicalcare.adapter.PacienteAdapter;
import br.com.unipar.medicalcare.dao.PacienteDAO;
import br.com.unipar.medicalcare.database.AppDatabase;
import br.com.unipar.medicalcare.database.RoomDatabaseOpenHelper;
import br.com.unipar.medicalcare.entities.Paciente;

public class ListaFiltraPaciente extends AppCompatActivity {

    private ListView pacientes;
    private List<Paciente> listaPacientes = new ArrayList<Paciente>();
    private EditText nome, horario, medico;
    private String nomeTemp, horarioTemp, medicoTemp, id;
    private CheckBox checkBox1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secundary);

        this.pacientes = findViewById(R.id.lstConsulta);

        // inicia segunda tela e chama a lista
        listaPacienteThread("", "", "", "");
    }

    // filtra registro // casso for vazio atribui "" para não dar erro com null.
    // chama listaThead
    public void listar(View view){
        nomeTemp = "";
        horarioTemp = "";
        medicoTemp = "";

        nome = findViewById(R.id.edPaciente);
        horario = findViewById(R.id.edAgendamento);
        medico = findViewById(R.id.edDoutor);

        if(!nome.getText().toString().isEmpty()) {
           nomeTemp = nome.getText().toString();
        }
        if(!horario.getText().toString().isEmpty()){
            horarioTemp = horario.getText().toString();
        }
        if(!medico.getText().toString().isEmpty()){
            medicoTemp = medico.getText().toString();
        }

        listaPacienteThread(nomeTemp, horarioTemp, medicoTemp, "");
    }

    // validar o checbox e apaga registro com id do checbox.
    public  void finalizar(View view){
        id = "";
        checkBox1 = findViewById(R.id.checkBox);

        if(checkBox1.isChecked()) {
            id = String.valueOf(checkBox1.getTag());

            listaPacienteThread("", "", "", id);
            limparFormulario();
        }else{
            mensagem("Para finalizar o atendimento \nDeve selecionar um registro");
        }
    }

    // chama intent primeira tela para voltar para main
    public  void voltar(View view){
        Intent intent = new Intent(this, MainActivity.class);
        Bundle b = new Bundle();
        intent.putExtras(b);
        startActivity(intent);
    }

    /* criando thread para lista pacinete para não trav tela.
       usando runOnUiThread(new Runnable() para chama classe fora
       classe lisat todos e filtar por nome ,horario e medico.
       aproveitando a mesma clase para deletar registro.
     */
    public void  listaPacienteThread(String nome, String horario, String medico, String id) {
        AppDatabase appDatabase = RoomDatabaseOpenHelper.getDatabase(new WeakReference<Context>(this));
        new Thread(new Runnable() {
            @Override
            public void run() {
                PacienteDAO pacienteDao = appDatabase.pacienteDAO();

                if(!id.equals("")){
                    pacienteDao.Deleta(id);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mensagem("Atendimento finalizado");
                        }
                    });
                }
                if(nome.equals("") && horario.equals("") && medico.equals("")){
                    listaPacientes = pacienteDao.listaPacientes();
                }
                else{
                    if(!nome.equals("")){
                        listaPacientes = pacienteDao.listaPacienteNome(nome);
                    }
                    else if(!horario.equals("")){
                        listaPacientes = pacienteDao.listaPacienteHorario(horario);
                    }
                    else if(!medico.equals("")){
                        listaPacientes = pacienteDao.listaPacienteMedico(medico);
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listaPaciente(listaPacientes);
                    }
                });
            }
        }).start();
    }

    // classe para atribuir a lista recebida por parametro na listaPacientes.
    public void listaPaciente(List<Paciente> listaTodos){

        final WeakReference<Context> weakReference = new WeakReference(this);
        PacienteAdapter pacienteAdapter = new PacienteAdapter(listaTodos, weakReference);
        pacientes.setAdapter(pacienteAdapter);
        this.listaPacientes = listaTodos;
    }

    public void mensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
    }

    public void limparFormulario(){
        nome.setText("");
        horario.setText("");
        medico.setText("");
    }
}
