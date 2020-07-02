package edu.kokhan.auzoo.di

import edu.kokhan.auzoo.database.FirestoreDatabase
import edu.kokhan.auzoo.ui.*
import edu.kokhan.auzoo.ui.splash.SplashActivity
import edu.kokhan.auzoo.ui.splash.SplashViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module


val appModule = module {

    single {
        FirestoreDatabase(get())
    }

    scope(named<MainActivity>()) {

    }

}

val viewModels = module {
    viewModel { SplashViewModel(get()) }
}

val fragmentModule = module {
    scope(named<AboutFragment>()) {

    }

    scope(named<AnimalFragment>()) {

    }

    scope(named<ArViewerFragment>()) {

    }

    scope(named<BookFragment>()) {

    }

    scope(named<MainActivity>()) {

    }

    scope(named<SplashActivity>()) {

    }

    scope(named<MenuFragment>()) {

    }

    scope(named<OptionsFragment>()) {

    }

    scope(named<ScanFragment>()) {

    }
}