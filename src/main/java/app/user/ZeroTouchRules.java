package app.user;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

public class ZeroTouchRules implements RuleUnitData {

    DataStore<User> users = DataSource.createStore();

    public DataStore<User> getUsers() {
        return users;
    }
}
