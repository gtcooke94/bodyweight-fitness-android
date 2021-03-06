package com.bodyweight.fitness.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.bodyweight.fitness.Exercise

import com.bodyweight.fitness.model.repository.RepositoryCategory
import com.bodyweight.fitness.model.repository.RepositoryExercise
import com.bodyweight.fitness.model.repository.RepositorySection
import com.bodyweight.fitness.stream.DialogType
import com.bodyweight.fitness.stream.UiEvent

import java.util.HashMap

import com.bodyweight.fitness.R
import com.bodyweight.fitness.inflate
import kotlinx.android.synthetic.main.activity_progress_card.view.*
import kotlinx.android.synthetic.main.activity_progress_title.view.*

class ProgressAdapter(private val mRepositoryCategory: RepositoryCategory) : RecyclerView.Adapter<ProgressPresenter>() {
    private val mItemViewMapping = HashMap<Int, RepositorySection>()
    private val mExerciseViewMapping = HashMap<Int, RepositoryExercise>()

    private var mTotalSize = 0

    init {
        /**
         * We loop over the sections in order to find out the item view id
         * for each section in the Recycler View.
         */
        var sectionId = 0
        var exerciseId = 1
        for (repositorySection in mRepositoryCategory.sections) {
            mItemViewMapping.put(sectionId, repositorySection)

            var numberOfExercises = 0

            for (repositoryExercise in repositorySection.exercises) {
                if (repositoryExercise.isVisible) {
                    mExerciseViewMapping.put(exerciseId, repositoryExercise)

                    exerciseId += 1
                    mTotalSize += 1

                    numberOfExercises++
                }
            }

            sectionId = sectionId + numberOfExercises + 1
            exerciseId += 1
            mTotalSize += 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressPresenter {
        if (viewType == 1) {
            val view = parent.inflate(R.layout.activity_progress_title)

            return ProgressTitlePresenter(view)
        }

        val view = parent.inflate(R.layout.activity_progress_card)

        return ProgressCardPresenter(view)
    }

    override fun onBindViewHolder(holder: ProgressPresenter, position: Int) {
        if (mItemViewMapping.containsKey(position)) {
            val presenter = holder as ProgressTitlePresenter

            presenter.bindView(mItemViewMapping[position]!!)
        } else if (mExerciseViewMapping.containsKey(position)) {
            val presenter = holder as ProgressCardPresenter

            presenter.bindView(mExerciseViewMapping[position]!!)
        }
    }

    override fun getItemCount(): Int {
        return mTotalSize
    }

    override fun getItemViewType(position: Int): Int {
        if (mItemViewMapping.containsKey(position)) {
            return 1
        }

        return 0
    }
}

abstract class ProgressPresenter(itemView: View) : RecyclerView.ViewHolder(itemView)

class ProgressCardPresenter(itemView: View) : ProgressPresenter(itemView) {
    fun bindView(repositoryExercise: RepositoryExercise) {
        itemView.toolbar.title = repositoryExercise.title
        itemView.toolbar.subtitle = repositoryExercise.description

        if (!Exercise.isCompleted(repositoryExercise)) {
            itemView.toolbar.subtitle = "Not completed"
        }

        itemView.view_button.setOnClickListener {
            UiEvent.showDialog(DialogType.LogWorkout, repositoryExercise.exerciseId)
        }
    }
}

class ProgressTitlePresenter(itemView: View) : ProgressPresenter(itemView) {
    fun bindView(repositorySection: RepositorySection) {
        if (layoutPosition == 0) {
            itemView.title.setPadding(
                    itemView.title.paddingLeft,
                    itemView.title.paddingLeft,
                    itemView.title.paddingRight,
                    itemView.title.paddingBottom)
        } else {
            itemView.title.setPadding(
                    itemView.title.paddingLeft,
                    itemView.title.paddingBottom,
                    itemView.title.paddingRight,
                    itemView.title.paddingBottom)
        }

        itemView.title.text = repositorySection.title
    }
}
