package io.github.palexdev.mfxcore.base.bindings;

import io.github.palexdev.mfxcore.base.bindings.base.Updater;

/**
 * Specialization of {@link Updater} that is also capable of mapping the input before passing it to the {@link Updater}.
 */
public class MappedUpdater<T, M> implements Updater<T> {
	//================================================================================
	// Properties
	//================================================================================
	protected Mapper<T, M> mapper;
	protected Updater<M> updater;

	//================================================================================
	// Constructors
	//================================================================================
	protected MappedUpdater() {
	}

	public MappedUpdater(Mapper<T, M> mapper, Updater<M> updater) {
		this.mapper = mapper;
		this.updater = updater;
	}

	//================================================================================
	// Methods
	//================================================================================
	@Override
	public void update(T oldValue, T newValue) {
		updater.update(
				mapper.apply(oldValue),
				mapper.apply(newValue)
		);
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * @return this updater's {@link Mapper}
	 */
	public Mapper<T, M> getMapper() {
		return mapper;
	}

	/**
	 * Sets this updater's {@link Mapper}.
	 */
	public MappedUpdater<T, M> setMapper(Mapper<T, M> mapper) {
		this.mapper = mapper;
		return this;
	}

	/**
	 * @return the {@link Updater} object
	 */
	public Updater<M> getUpdater() {
		return updater;
	}

	/**
	 * Sets the updater.
	 */
	public MappedUpdater<T, M> setUpdater(Updater<M> updater) {
		this.updater = updater;
		return this;
	}

	//================================================================================
	// Builder
	//================================================================================
	public static class Builder<T, M> {
		private final MappedUpdater<T, M> mappedUpdater = new MappedUpdater<>();

		public Builder<T, M> updater(Updater<M> updater) {
			mappedUpdater.updater = updater;
			return this;
		}

		public Builder<T, M> mapper(Mapper<T, M> mapper) {
			mappedUpdater.mapper = mapper;
			return this;
		}

		public MappedUpdater<T, M> get() {
			return mappedUpdater;
		}
	}
}
