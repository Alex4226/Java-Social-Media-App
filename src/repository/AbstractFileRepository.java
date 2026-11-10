package repository;

import domain.Entity;
import domain.validators.ValidationException;
import domain.validators.Validator;
import repository.exceptions.RepositoryException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.Optional;

/**
 * Abstract class that manages file loading and saving for every entity type. Inherites InMemoryRepository
 * @param <ID> template for id data types
 * @param <E> template for entity types
 */
public abstract class AbstractFileRepository<ID,E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    private String filename;

    /**
     * Abstract constructor for a file repository with specified validator
     * @param validator used validator for validation of the entities before loading
     * @param filename the filename / pathname of the file that saves the entities
     */
    public AbstractFileRepository(Validator<E> validator, String filename) {
        super(validator);
        this.filename = filename;

    }

    /**
     * Receives a list of attributes that construct an entity
     * @param attributes the list of attributes received for the entity
     * @return the extracted entity
     */
    public abstract E extractEntity(List<String> attributes);

    /**
     * Receives an E type object and returns a line formatted as a string
     * @param entity entity that is serialized
     * @return line formatted as a string
     */
    protected abstract String createEntityAsString(E entity);

    /**
     * Loads data into repository from file
     */
    protected void loadData() {
        Path path = Paths.get(filename);
        if (!Files.exists(path)) {
            return;
        }

        try (Stream<String> linesStream = Files.lines(path, StandardCharsets.UTF_8)) {
            List<String> allLines = linesStream.collect(Collectors.toList());

            IntStream.range(0, allLines.size())
                    .forEach(index -> {
                        int lineNumber = index + 1;
                        String line = allLines.get(index);

                        if (line != null && !line.trim().isEmpty()) {
                            try {
                                String[] parts = line.split(";", -1);
                                List<String> attributes = Arrays.asList(parts);
                                E entity = extractEntity(attributes);
                                super.add(entity);
                            } catch (ValidationException | IllegalArgumentException e) {
                                System.err.println("Line" + lineNumber + " is corrupt or invalid: " + e.getMessage());
                            }
                        }
                    });
        } catch (IOException e) {
            throw new RepositoryException("Error reading from file: " + filename, e);
        }
    }

    /**
     * Writes data to file from memory. Transforms entities to string
     */
    private void writeToFile() {
        Path path = Paths.get(filename);
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            List<String> serializedLines = StreamSupport.stream(super.getAll().spliterator(), false)
                    .map(this::createEntityAsString)
                    .collect(Collectors.toList());

            Files.write(path, serializedLines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RepositoryException("Error writing to file: " + filename, e);
        }
    }

    /**
     * Adds an entity in the collection. Saves to file if it adds successfully
     * @param entity the entity that needs to be added
     * @return {@code null} if the addition was successful or the existing entity if the id already exists
     */
    @Override
    public Optional<E> add(E entity) {
        Optional<E> result = super.add(entity);
        if (result.isEmpty()) {
            writeToFile();
        }
        return result;
    }

    /**
     * Deletes an entity from the collection. Saves to file if it deletes successfully
     * @param id id of the entity that needs to be deleted
     * @return elminated entity or {@code null} if it doesn't exist
     */
    @Override
    public Optional<E> delete(ID id) {
        Optional<E> result = super.delete(id);
        result.ifPresent(e -> writeToFile());
        return result;
    }

    /**
     * Updates an entity from the collection. Saves to file if updates successfully
     * @param entity updated entity
     * @return {@code null} if the update was successful or the received entity if it wasn't found in the memory
     */
    @Override
    public Optional<E> update(E entity) {
        Optional<E> result = super.update(entity);
        if (result.isEmpty()) {
            writeToFile();
        }
        return result;
    }
}
