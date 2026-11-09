package com.example.lembraai;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lembraai.model.Tarefa;

import java.util.List;

public class TarefaAdapter extends BaseAdapter {

    private Context context;
    private List<Tarefa> tarefas;

    public TarefaAdapter(Context context, List<Tarefa> tarefas) {
        this.context = context;
        this.tarefas = tarefas;
    }

    @Override
    public int getCount() {
        return tarefas.size();
    }

    @Override
    public Object getItem(int position) {
        return tarefas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return tarefas.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Cria a linha usando o layout item_tarefa.xml
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_tarefa, parent, false);
        }

        // Localiza os TextViews da linha
        TextView txtTitulo = convertView.findViewById(R.id.txtTituloTarefa);
        TextView txtDescricao = convertView.findViewById(R.id.txtDescricaoTarefa);

        // Obtém a tarefa da posição atual
        Tarefa tarefa = tarefas.get(position);

        // Mostra os dados da tarefa
        txtTitulo.setText(tarefa.titulo);
        txtDescricao.setText(tarefa.descricao);

        return convertView;
    }
}