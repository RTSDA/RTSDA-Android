package org.rtsda.android.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.rtsda.android.data.repository.BulletinRepositoryImpl
import org.rtsda.android.data.repository.EventRepository
import org.rtsda.android.data.repository.EventRepositoryImpl
import org.rtsda.android.data.repository.MessageRepository
import org.rtsda.android.data.repository.MessageRepositoryImpl
import org.rtsda.android.data.repository.VersesRepository
import org.rtsda.android.data.repository.VersesRepositoryImpl
import org.rtsda.android.data.MessagesRepository
import org.rtsda.android.data.MessagesRepositoryImpl
import org.rtsda.android.domain.repository.BulletinRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMessagesRepository(
        messagesRepositoryImpl: MessagesRepositoryImpl
    ): MessagesRepository

    @Binds
    @Singleton
    abstract fun bindBulletinRepository(
        bulletinRepositoryImpl: BulletinRepositoryImpl
    ): BulletinRepository

    @Binds
    @Singleton
    abstract fun bindEventRepository(
        eventRepositoryImpl: EventRepositoryImpl
    ): EventRepository

    @Binds
    @Singleton
    abstract fun bindMessageRepository(
        messageRepositoryImpl: MessageRepositoryImpl
    ): MessageRepository

    @Binds
    @Singleton
    abstract fun bindVersesRepository(
        versesRepositoryImpl: VersesRepositoryImpl
    ): VersesRepository
} 