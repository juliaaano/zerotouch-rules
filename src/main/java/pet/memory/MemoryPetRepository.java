package pet.memory;

import static java.util.Collections.newSetFromMap;
import static java.util.Collections.synchronizedMap;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import pet.Pet;
import pet.PetRepository;

@ApplicationScoped
public class MemoryPetRepository implements PetRepository {

    private Set<Pet> pets = newSetFromMap(synchronizedMap(new LinkedHashMap<>()));

    public MemoryPetRepository() {
        pets.add(new Pet("Dog", "Labrador", "Max"));
        pets.add(new Pet("Dog", "Stray", null));
        pets.add(new Pet("Cat", "Persian Cat", "Garfield"));
    }

    @Override
    public Optional<Pet> find(final String identifier) {
        return pets.stream().filter(pet -> pet.getIdentifier().equals(identifier)).findFirst();
    }

    @Override
    public Set<Pet> find() {
        return pets;
    }

    @Override
    public String create(final Pet pet) {
        pets.add(pet);
        return pet.getIdentifier();
    }

    @Override
    public void delete(final String identifier) {
        pets.removeIf(existingPet -> existingPet.getIdentifier().contentEquals(identifier));
    }

}
