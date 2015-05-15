package com.blackjackquiz.app.ui.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.blackjackquiz.app.R;
import com.blackjackquiz.app.logger.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SolutionTableFragment extends KeyEventFragment
{
    public SolutionTableFragment()
    {
        m_solutionTableLoader = new ThreadPoolExecutor(1,
                                                       1,
                                                       5000L,
                                                       TimeUnit.SECONDS,
                                                       new LinkedBlockingQueue<Runnable>());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_solution_table, container, false);
        m_solutionTableImg = (ImageView) rootView.findViewById(R.id.solution_table_img);

        if (m_solutionTableDrawable == null)
        {
            m_solutionTableDrawable = m_solutionTableLoader.submit(new LoadSolutionTableTask());
        }

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        for (; ; )
        {
            try
            {
                m_solutionTableImg.setImageDrawable(m_solutionTableDrawable.get());
                break;
            }
            catch (Exception e)
            {
                Logger.log(e, "Failed to load drawable for the solution table, trying again...");
            }
        }
    }

    private class LoadSolutionTableTask implements Callable<Drawable>
    {
        @Override
        public Drawable call() throws Exception
        {
            return getActivity().getResources().getDrawable(R.drawable.black_jack_solution_table);
        }
    }

    private ImageView        m_solutionTableImg;
    private Future<Drawable> m_solutionTableDrawable;

    private final ThreadPoolExecutor m_solutionTableLoader;
}
