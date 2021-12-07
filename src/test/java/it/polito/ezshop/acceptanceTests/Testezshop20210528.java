package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.*;
import it.polito.ezshop.data.EZShopInterface;
import it.polito.ezshop.exceptions.*;
import org.junit.*;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

public class Testezshop20210528 {
    private static EZShopInterface ezshop;
    private int adminId;
    private String adminBaseUsername = "ADMIN";
    private String adminBasePwd = "ADMIN_PWD";


    private String username1 = "TestUsr1";
    private String username2 = "TestUsr2";
    private String username3 = "TestUsr3";
    private String userPwd = "TestUsrPwd";
    private String cashier = "Cashier";
    private String shopManager = "ShopManager";
    private String admin = "Administrator";

    private String productDescr1 = "testProduct1";
    private String productDescr2 = "testProduct2";
    private String barCode = "000012354788";
    private String barCode2 = "000055555555";
    private String invalidBarCode = "12354780";
    private double pricePerUnit = 0.50;
    private double pricePerUnit2 = 1.50;
    private double orderPricePerUnit = 0.25;
    private double orderPricePerUnit2 = 1.00;

    private String note1 = "test description";
    private String note2 = "description product";
    private String note3 = "type type";
    private String emptyNote = "";

    private int quantity = 10;
    private int quantity2 = 1;

    private String location1 = "10-A-1";
    private String location2 = "1-Z-10";
    private String invalidLocation1 = "A-A-0";
    private String invalidLocation2 = "0-A-A";
    private String invalidLocation3 = "A-A-A";

    private String customerName1 = "testCustomerName 1";
    private String customerName2 = "testCustomerName 2";
    private String customerCard1 = "1000011110";
    private String customerCard2 = "1000011111";
    private String invalidCustomerCard = "100001111a";
    private int point1 = 0;
    private int point2 = 100;

    private double discountRate = 0.5;
    private double invalidDiscountRate = 1.01;

    private double cash = 5.00;
    private String creditCard150 = "4485370086510891";
    private String creditCard10 = "5100293991053009";
    private String creditCard0 = "4716258050958645";
    private String notRegisteredCreditCard = "4485232344883462";
    private String invalidCreditCard = "4485370086510898";

    private final String BALANCE_ORDER = "ORDER";
    private final String BALANCE_SALE = "SALE";
    private final String BALANCE_RETURN = "RETURN";
    private final String BALANCE_CREDIT = "CREDIT";
    private final String BALANCE_DEBIT = "DEBIT";

    private final String ORDER_ISSUED = "ISSUED";
    private final String ORDER_PAYED = "PAYED";
    private final String ORDER_COMPLETED = "COMPLETED";


    @BeforeClass
    public static void setUpEzShop() {
        ezshop = new EZShop();
    }

    @AfterClass
    public static void clearEzShop(){
        ezshop.reset();
    }

    private String getErrorMsg(String testName, String msg) {
        return "Error in test " + testName + ": " + msg;
    }

    private boolean isValidCreditCard(String creditCard){
        try {
            Pattern pattern = Pattern.compile("[0-9]+");
            if (!pattern.matcher(creditCard).matches())
                return false;
            int last = 0;
            //pad with zeros to reach 14 digits
            switch (creditCard.length()) {
                case 13:
                case 14:
                case 15:
                case 16:
                case 19:
                    last = creditCard.charAt(creditCard.length()-1);
                    break;
                default:
                    //invalid
                    return false;
            }
            int sum = 0;
            int val = 0;
            int size = creditCard.length()-1;
            for (int i = creditCard.length()-2; i >= 0; i--) {
                if ((size-i) % 2 == 0) {
                    sum += Character.getNumericValue(creditCard.charAt(i));
                } else {
                    val = Character.getNumericValue(creditCard.charAt(i));
                    val = val * 2;
                    if(val > 9)
                        val -= 9;
                    sum += val;
                }
            }
            return last == (sum % 10);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private int computePointsFromPrice(double price){
        return (int) (price/10);
    }

    private boolean isValidCustomerCard(String card) {
        Pattern pattern = Pattern.compile("[\\d]{10}");
        return pattern.matcher(card).matches();
    }

    private boolean isBarCodeValid(String code) {
        try {
            Pattern pattern = Pattern.compile("[0-9]+");
            if (!pattern.matcher(code).matches())
                return false;
            //pad with zeros to reach 14 digits
            switch (code.length()) {
                case 8:
                    code = "000000" + code;
                    break;
                case 12:
                    code = "00" + code;
                    break;
                case 13:
                    code = "0" + code;
                    break;
                case 14:
                    break;
                default:
                    //invalid
                    return false;
            }
            int sum = 0;
            for (int i = 0; i < 13; i++) {
                if ((i+1) % 2 == 0) {
                    sum += (Character.getNumericValue(code.charAt(i)) * 3);
                } else {
                    sum += Character.getNumericValue(code.charAt(i));
                }
            }
            return Character.getNumericValue(code.charAt(13)) == ((10 - (sum % 10)) % 10);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isValidPosition(String newPos) {
        if(newPos == null || newPos.isEmpty())
            return false;
        Pattern pattern = Pattern.compile("[a-z,A-Z]+-[\\d]+");
        return pattern.matcher(newPos).matches();
    }

    @Before
    public void setup() {
        ezshop.reset();
        try {
            adminId = ezshop.createUser(adminBaseUsername,adminBasePwd,admin);
            ezshop.login(adminBaseUsername,adminBasePwd);
        } catch (InvalidUsernameException | InvalidPasswordException | InvalidRoleException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateUser() {
        try {
            int id = ezshop.createUser(username1, userPwd, cashier);
            Assert.assertTrue(getErrorMsg("testCreateUser", "returned id for role " + cashier + " is less then or equal to 0"), id > 0);
            id = ezshop.createUser(username2, userPwd, shopManager);
            Assert.assertTrue(getErrorMsg("testCreateUser", "returned id for role " + shopManager + " is less then or equal to 0"), id > 0);
            id = ezshop.createUser(username3, userPwd, admin);
            Assert.assertTrue(getErrorMsg("testCreateUser", "returned id for role " + admin + " is less then or equal to 0"), id > 0);
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateUser", "The inserted roles should not be considered as invalid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateUser", "The inserted password should not be considered as invalid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateUser", "The inserted usernames should not be considered as invalid"));
        }
    }

    @Test
    public void testCreateExistingUser() {
        try {
            int id = ezshop.createUser(username1, userPwd, cashier);
            Assert.assertTrue(getErrorMsg("testCreateExistingUser", "returned id for role " + cashier + " is less then or equal to 0"), id > 0);
            id = ezshop.createUser(username1, userPwd, shopManager);
            Assert.assertEquals(getErrorMsg("testCreateExistingUser", "returned id should be -1"), -1, id);
            id = ezshop.createUser(username1, userPwd, admin);
            Assert.assertEquals(getErrorMsg("testCreateExistingUser", "returned id should be -1"), -1, id);
            id = ezshop.createUser(username1, userPwd, cashier);
            Assert.assertEquals(getErrorMsg("testCreateExistingUser", "returned id should be -1"), -1, id);
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateExistingUser", "The inserted roles should not be considered as invalid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateExistingUser", "The inserted password should not be considered as invalid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateExistingUser", "The inserted usernames should not be considered as invalid"));
        }
    }

    @Test(expected = InvalidUsernameException.class)
    public void testGetUserEmptyUsername() throws InvalidUsernameException {
        try {
            int id = ezshop.createUser("", userPwd, cashier);
            Assert.fail(getErrorMsg("testGetUserEmptyUsername", "Empty username should be considered as invalid"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserEmptyUsername", "The inserted roles should not be considered as invalid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserEmptyUsername", "The inserted password should not be considered as invalid"));
        }
    }

    @Test(expected = InvalidUsernameException.class)
    public void testGetUserNullUsername() throws InvalidUsernameException {
        try {
            int id = ezshop.createUser(null, userPwd, cashier);
            Assert.fail(getErrorMsg("testGetUserNullUsername", "Null username should be considered as invalid"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserNullUsername", "The inserted roles should not be considered as invalid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserNullUsername", "The inserted password should not be considered as invalid"));
        }
    }

    @Test(expected = InvalidPasswordException.class)
    public void testGetUserEmptyPassword() throws InvalidPasswordException {
        try {
            int id = ezshop.createUser(username1, "", cashier);
            Assert.fail(getErrorMsg("testGetUserEmptyPassword", "Empty Password should be considered as invalid"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserEmptyPassword", "The inserted roles should not be considered as invalid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserEmptyPassword", "The inserted username should not be considered as invalid"));
        }
    }

    @Test(expected = InvalidPasswordException.class)
    public void testGetUserNullPassword() throws InvalidPasswordException {
        try {
            int id = ezshop.createUser(username1, null, cashier);
            Assert.fail(getErrorMsg("testGetUserNullUsername", "Null Password should be considered as invalid"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserEmptyPassword", "The inserted roles should not be considered as invalid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserEmptyPassword", "The inserted username should not be considered as invalid"));
        }
    }

    @Test(expected = InvalidRoleException.class)
    public void testGetUserEmptyRole() throws InvalidRoleException {
        try {
            int id = ezshop.createUser(username1, userPwd, "");
            Assert.fail(getErrorMsg("testGetUserEmptyRole", "Empty Role should be considered as invalid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserEmptyRole", "The inserted password should not be considered as invalid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserEmptyRole", "The inserted username should not be considered as invalid"));
        }
    }

    @Test(expected = InvalidRoleException.class)
    public void testGetUserNullRole() throws InvalidRoleException {
        try {
            int id = ezshop.createUser(username1, userPwd, null);
            Assert.fail(getErrorMsg("testGetUserNullUsername", "Null role should be considered as invalid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserNullRole", "The inserted password should not be considered as invalid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserNullRole", "The inserted username should not be considered as invalid"));
        }
    }

    @Test(expected = InvalidRoleException.class)
    public void testGetUserNotExistingRole() throws InvalidRoleException {
        try {
            int id = ezshop.createUser(username1, userPwd, "role");
            Assert.fail(getErrorMsg("testGetUserNotExistingRole", " \"role\" should not be considered as a valid role"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserNotExistingRole", "The inserted password should not be considered as invalid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserNotExistingRole", "The inserted username should not be considered as invalid"));
        }
    }

    @Test
    public void testDeleteUser() {
        try {
            int id = ezshop.createUser(username1, userPwd, shopManager);
            Assert.assertTrue(getErrorMsg("testDeleteUser", "delete of the inserted user should succeed"), ezshop.deleteUser(id));
            Assert.assertFalse(getErrorMsg("testDeleteUser", "delete of a deleted user should fail"), ezshop.deleteUser(id));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteUser", "The inserted role should not be invalid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteUser", "The inserted password should not be invalid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteUser", "The inserted username should not be invalid"));
        } catch (InvalidUserIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteUser", "The inserted id should not be invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteUser", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidUserIdException.class)
    public void testDeleteUserZeroId() throws InvalidUserIdException {
        try {
            ezshop.deleteUser(0);
            Assert.fail(getErrorMsg("testDeleteUserZeroId", "delete with invalid id should throw an exception"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteUserZeroId", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidUserIdException.class)
    public void testDeleteUserNegativeId() throws InvalidUserIdException {
        try {
            ezshop.deleteUser(-1);
            Assert.fail(getErrorMsg("testDeleteUserNegativeId", "delete with invalid id should throw an exception"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteUserNegativeId", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidUserIdException.class)
    public void testDeleteUserNullId() throws InvalidUserIdException {
        try {
            ezshop.deleteUser(null);
            Assert.fail(getErrorMsg("testDeleteUserNullId", "delete with invalid id should throw an exception"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteUserNullId", "The logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testDeleteUserNotLogged() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.deleteUser(1);
            Assert.fail(getErrorMsg("testDeleteUserNotLogged", "delete without a login should throw an exception"));
        } catch (InvalidUserIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteUserNotLogged", "User id should be considered valid"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testDeleteUserNotAdmin1() throws UnauthorizedException {
        try {
            ezshop.createUser(username1,userPwd,cashier);
            ezshop.logout();
            ezshop.login(username1,userPwd);
            ezshop.deleteUser(1);
            Assert.fail(getErrorMsg("testDeleteUserNotAdmin1", "delete made by a non admin account throw an exception"));
        } catch (InvalidUserIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteUserNotAdmin1", "User id should be considered valid"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteUserNotAdmin1", "User role should be considered valid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteUserNotAdmin1", "User pwd should be considered valid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteUserNotAdmin1", "Username should be considered valid"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testDeleteUserNotAdmin2() throws UnauthorizedException {
        try {
            ezshop.createUser(username1,userPwd,shopManager);
            ezshop.logout();
            ezshop.login(username1,userPwd);
            ezshop.deleteUser(1);
            Assert.fail(getErrorMsg("testDeleteUserNotAdmin2", "delete made by a non admin account throw an exception"));
        } catch (InvalidUserIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteUserNotAdmin2", "User id should be considered valid"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteUserNotAdmin2", "User role should be considered valid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteUserNotAdmin2", "User pwd should be considered valid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteUserNotAdmin2", "Username should be considered valid"));
        }
    }

    @Test
    public void testGetAllUsers() {
        try {
            List<User> list = ezshop.getAllUsers();
            Assert.assertEquals(getErrorMsg("testGetAllUsers", "there should be 1 user"), 1, list.size());

            int id1 = ezshop.createUser(username1, userPwd, cashier);
            int id2 = ezshop.createUser(username2, userPwd, admin);
            int id3 = ezshop.createUser(username3, userPwd, shopManager);
            list = ezshop.getAllUsers();
            Assert.assertEquals(getErrorMsg("testGetAllUsers", "there should be 4 users"), 4, list.size());
            boolean found1 = false;
            boolean found2 = false;
            boolean found3 = false;
            boolean found4 = false;
            for(User u : list){
                if(u.getId() == id1 && !found1){
                    Assert.assertEquals(getErrorMsg("testGetAllUsers", "user " + id1 + " should have username " + username1), username1, u.getUsername());
                    Assert.assertEquals(getErrorMsg("testGetAllUsers", "user " + id1 + " should have role " + cashier), cashier, u.getRole());
                    Assert.assertEquals(getErrorMsg("testGetAllUsers", "user " + id1 + " should have id " + id1), id1, u.getId().intValue());
                    Assert.assertEquals(getErrorMsg("testGetAllUsers", "user " + id1 + " should have password " + userPwd), userPwd, u.getPassword());
                    found1 = true;
                } else if( u.getId() == id2 && !found2){
                    Assert.assertEquals(getErrorMsg("testGetAllUsers", "user " + id1 + " should have username " + username2), username2, u.getUsername());
                    Assert.assertEquals(getErrorMsg("testGetAllUsers", "user " + id1 + " should have role " + admin), admin, u.getRole());
                    Assert.assertEquals(getErrorMsg("testGetAllUsers", "user " + id1 + " should have id " + id2), id2, u.getId().intValue());
                    Assert.assertEquals(getErrorMsg("testGetAllUsers", "user " + id1 + " should have password " + userPwd), userPwd, u.getPassword());
                    found2 = true;
                } else if( u.getId() == id3 && !found3){
                    Assert.assertEquals(getErrorMsg("testGetAllUsers", "user " + id1 + " should have username " + username3), username3, u.getUsername());
                    Assert.assertEquals(getErrorMsg("testGetAllUsers", "user " + id1 + " should have role " + shopManager), shopManager, u.getRole());
                    Assert.assertEquals(getErrorMsg("testGetAllUsers", "user " + id1 + " should have id " + id3), id3, u.getId().intValue());
                    Assert.assertEquals(getErrorMsg("testGetAllUsers", "user " + id1 + " should have password " + userPwd), userPwd, u.getPassword());
                    found3 = true;
                } else if( u.getId() == adminId && !found4){
                    Assert.assertEquals(getErrorMsg("testGetAllUsers", "user " + adminId + " should have username " + adminBaseUsername), adminBaseUsername, u.getUsername());
                    Assert.assertEquals(getErrorMsg("testGetAllUsers", "user " + adminId + " should have role " + admin), admin, u.getRole());
                    Assert.assertEquals(getErrorMsg("testGetAllUsers", "user " + adminId + " should have id " + adminId), adminId, u.getId().intValue());
                    Assert.assertEquals(getErrorMsg("testGetAllUsers", "user " + adminId + " should have password " + adminBasePwd), adminBasePwd, u.getPassword());
                    found4 = true;
                }
            }
            Assert.assertTrue(getErrorMsg("testGetAllUsers","Not all users were found"),found1&&found2&&found3&&found4);
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetAllUsers", "The inserted roles should be valid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetAllUsers", "The inserted password should be valid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetAllUsers", "The inserted usernames should be valid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetAllUsers", "The logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetAllUsersNotLogged() throws UnauthorizedException {
        ezshop.logout();
        List<User> list = ezshop.getAllUsers();
        Assert.fail(getErrorMsg("testGetAllUsersNotLogged", "The operation should fail"));
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetAllUsersNotAuthorized1() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.createUser(username1, userPwd, cashier);
            ezshop.login(username1, userPwd);
            List<User> list = ezshop.getAllUsers();
            Assert.fail(getErrorMsg("testGetAllUsersNotAuthorized1", "The operation should fail"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetAllUsersNotAuthorized1", "The inserted role should be valid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetAllUsersNotAuthorized1", "The inserted password should be valid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetAllUsersNotAuthorized1", "The inserted username should be valid"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetAllUsersNotAuthorized2() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.createUser(username1, userPwd, shopManager);
            ezshop.login(username1, userPwd);
            List<User> list = ezshop.getAllUsers();
            Assert.fail(getErrorMsg("testGetAllUsersNotAuthorized2", "The operation should fail"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetAllUsersNotAuthorized2", "The inserted role should be valid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetAllUsersNotAuthorized2", "The inserted password should be valid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetAllUsersNotAuthorized2", "The inserted username should be valid"));
        }
    }

    @Test
    public void testGetUser() {
        try {
            User res = ezshop.getUser(adminId+1);
            Assert.assertNull(getErrorMsg("testGetUser", "Null should be returned when a user does not exist"), res);
            int id = ezshop.createUser(username1, userPwd, cashier);
            res = ezshop.getUser(id);
            Assert.assertEquals(getErrorMsg("testGetUser", "User " + id + " should have username " + username1), username1, res.getUsername());
            Assert.assertEquals(getErrorMsg("testGetUser", "User " + id + " should have id " + id), id, res.getId().intValue());
            Assert.assertEquals(getErrorMsg("testGetUser", "User " + id + " should have password " + userPwd), userPwd, res.getPassword());
            Assert.assertEquals(getErrorMsg("testGetUser", "User " + id + " should have role " + cashier), cashier, res.getRole());
        } catch (InvalidUserIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUser", "The id should not be considered as invalid"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUser", "The role should not be considered as invalid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUser", "The password should not be considered as invalid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUser", "The username should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUser", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidUserIdException.class)
    public void testGetUserZeroId() throws InvalidUserIdException {
        try {
            ezshop.getUser(0);
            Assert.fail(getErrorMsg("testGetUserZeroId", "Zero should be considered an invalid value"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserZeroId", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidUserIdException.class)
    public void testGetUserNegativeId() throws InvalidUserIdException {
        try {
            ezshop.getUser(-1);
            Assert.fail(getErrorMsg("testGetUserNegativeId", "A negative id should be considered an invalid value"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserNegativeId", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidUserIdException.class)
    public void testGetUserNullId() throws InvalidUserIdException {
        try {
            ezshop.getUser(null);
            Assert.fail(getErrorMsg("testGetUserNullId", "Null should be considered an invalid value"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserNullId", "The logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetUserNotLogged() throws UnauthorizedException {
        try{
            ezshop.logout();
            ezshop.getUser(adminId);
            Assert.fail(getErrorMsg("testGetUserNotLogged", "The operation should fail"));
        } catch (InvalidUserIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserNotLogged", "The id should not be considered as invalid"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetUserNotAuthorized1() throws UnauthorizedException {
        try{
            ezshop.logout();
            ezshop.createUser(username1,userPwd,cashier);
            ezshop.login(username1,userPwd);
            ezshop.getUser(adminId);
            Assert.fail(getErrorMsg("testGetUserNotAuthorized1", "The operation should fail"));
        } catch (InvalidUserIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserNotAuthorized1", "The id should not be considered as invalid"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserNotAuthorized1", "The role should not be considered as invalid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserNotAuthorized1", "The password should not be considered as invalid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserNotAuthorized1", "The username should not be considered as invalid"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetUserNotAuthorized2() throws UnauthorizedException {
        try{
            ezshop.logout();
            ezshop.createUser(username1,userPwd,shopManager);
            ezshop.login(username1,userPwd);
            ezshop.getUser(adminId);
            Assert.fail(getErrorMsg("testGetUserNotAuthorized2", "The operation should fail"));
        } catch (InvalidUserIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserNotAuthorized2", "The id should not be considered as invalid"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserNotAuthorized2", "The role should not be considered as invalid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserNotAuthorized2", "The password should not be considered as invalid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserNotAuthorized2", "The username should not be considered as invalid"));
        }
    }

    @Test
    public void testUpdateUserRights() {
        try {
            Assert.assertFalse(getErrorMsg("testUpdateUserRights", "Update role of non existing user should return false"), ezshop.updateUserRights(100, cashier));
            int id = ezshop.createUser(username1, userPwd, cashier);
            Assert.assertTrue(getErrorMsg("testUpdateUserRights", "Update role of user " + id + " should return true"), ezshop.updateUserRights(id, admin));
            Assert.assertTrue(getErrorMsg("testUpdateUserRights", "Update role of user " + id + " should return true"), ezshop.updateUserRights(id, shopManager));
            Assert.assertTrue(getErrorMsg("testUpdateUserRights", "Update role of user " + id + " should return true"), ezshop.updateUserRights(id, cashier));
        } catch (InvalidUserIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateUserRights", "The id should not be considered as invalid"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateUserRights", "The role should not be considered as invalid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateUserRights", "The username should not be considered as invalid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateUserRights", "The password should not be considered as invalid"));
        }  catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateUserRights", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidUserIdException.class)
    public void testUpdateUserRightsZeroID() throws InvalidUserIdException {
        try {
            ezshop.updateUserRights(0, cashier);
            Assert.fail(getErrorMsg("testUpdateUserRightsZeroId", "The id should not be considered as valid"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateUserRightsZeroId", "The role should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateUserRightsZeroId", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidUserIdException.class)
    public void testUpdateUserRightsNegativeID() throws InvalidUserIdException {
        try {
            ezshop.updateUserRights(-1, cashier);
            Assert.fail(getErrorMsg("testUpdateUserRightsNegativeId", "The id should not be considered as valid"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateUserRightsNegativeId", "The role should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateUserRightsNegativeId", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidUserIdException.class)
    public void testUpdateUserRightsNullID() throws InvalidUserIdException {
        try {
            ezshop.updateUserRights(null, cashier);
            Assert.fail(getErrorMsg("testUpdateUserRightsNullId", "The id should not be considered as valid"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateUserRightsNullId", "The role should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateUserRightsNullId", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidRoleException.class)
    public void testUpdateUserRightsInvalidRole() throws InvalidRoleException {
        try {
            ezshop.updateUserRights(100, "NotExistingRole");
            Assert.fail(getErrorMsg("testUpdateUserRightsInvalidRole", "The role should not be considered as valid"));
        } catch (InvalidUserIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateUserRightsInvalidRole", "The id should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateUserRightsInvalidRole", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidRoleException.class)
    public void testUpdateUserRightsEmptyRole() throws InvalidRoleException {
        try {
            ezshop.updateUserRights(100, "");
            Assert.fail(getErrorMsg("testUpdateUserRightsEmptyRole", "The role should not be considered as valid"));
        } catch (InvalidUserIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateUserRightsEmptyRole", "The id should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateUserRightsEmptyRole", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidRoleException.class)
    public void testUpdateUserRightsNullRole() throws InvalidRoleException {
        try {
            ezshop.updateUserRights(100, null);
            Assert.fail(getErrorMsg("testUpdateUserRightsNullRole", "The role should not be considered as valid"));
        } catch (InvalidUserIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateUserRightsNullRole", "The id should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateUserRightsNullRole", "The logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testUpdateUserRightsNotLogged() throws UnauthorizedException {
        try{
            ezshop.logout();
            ezshop.updateUserRights(adminId,cashier);
            Assert.fail(getErrorMsg("testGetUserNotLogged", "The operation should fail"));
        } catch (InvalidUserIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserNotLogged", "The id should not be considered as invalid"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserNotLogged", "The role should not be considered as invalid"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testUpdateUserRightsNotAuthorized1() throws UnauthorizedException {
        try{
            ezshop.logout();
            ezshop.createUser(username1,userPwd,cashier);
            ezshop.login(username1,userPwd);
            ezshop.updateUserRights(adminId,cashier);
            Assert.fail(getErrorMsg("testGetUserNotAuthorized1", "The operation should fail"));
        } catch (InvalidUserIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserNotAuthorized1", "The id should not be considered as invalid"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserNotAuthorized1", "The role should not be considered as invalid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserNotAuthorized1", "The password should not be considered as invalid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserNotAuthorized1", "The username should not be considered as invalid"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testUpdateUserRightsNotAuthorized2() throws UnauthorizedException {
        try{
            ezshop.logout();
            ezshop.createUser(username1,userPwd,shopManager);
            ezshop.login(username1,userPwd);
            ezshop.updateUserRights(adminId,cashier);
            Assert.fail(getErrorMsg("testGetUserNotAuthorized2", "The operation should fail"));
        } catch (InvalidUserIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserNotAuthorized2", "The id should not be considered invalid"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserNotAuthorized2", "The role should not be considered invalid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserNotAuthorized2", "The password should not be considered invalid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetUserNotAuthorized2", "The username should not be considered invalid"));
        }
    }

    @Test
    public void testLogin(){
        try{
            Assert.assertNull(getErrorMsg("testLogin", "The login should fail"), ezshop.login(username1,userPwd));
            ezshop.logout();
            Assert.assertNull(getErrorMsg("testLogin", "The login should fail"), ezshop.login(adminBaseUsername,userPwd));
            Assert.assertNull(getErrorMsg("testLogin", "The login should fail"), ezshop.login(username1,adminBasePwd));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testLogin", "The username should not be considered invalid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testLogin", "The password should not be considered invalid"));
        }
    }

    @Test(expected = InvalidUsernameException.class)
    public void testLoginEmptyUsr() throws InvalidUsernameException {
        try{
            ezshop.logout();
            ezshop.login("",userPwd);
            Assert.fail(getErrorMsg("testLoginEmptyUsr", "The operation should fail"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testLoginEmptyUsr", "The password should not be considered invalid"));
        }
    }

    @Test(expected = InvalidUsernameException.class)
    public void testLoginNullUsr() throws InvalidUsernameException {
        try{
            ezshop.logout();
            ezshop.login(null,userPwd);
            Assert.fail(getErrorMsg("testLoginNullUsr", "The operation should fail"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testLoginNullUsr", "The password should not be considered invalid"));
        }
    }

    @Test(expected = InvalidPasswordException.class)
    public void testLoginEmptyPwd() throws InvalidPasswordException {
        try{
            ezshop.logout();
            ezshop.login(adminBaseUsername,"");
            Assert.fail(getErrorMsg("testLoginEmptyPwd", "The operation should fail"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testLoginEmptyPwd", "The username should not be considered invalid"));
        }
    }

    @Test(expected = InvalidPasswordException.class)
    public void testLoginNullPwd() throws InvalidPasswordException {
        try{
            ezshop.logout();
            ezshop.login(adminBaseUsername,null);
            Assert.fail(getErrorMsg("testLoginNullPwd", "The operation should fail"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testLoginNullPwd", "The username should not be considered invalid"));
        }
    }

    @Test
    public void testLogout(){
        Assert.assertTrue(getErrorMsg("testLogout", "The logout should not fail"),ezshop.logout());
        Assert.assertFalse(getErrorMsg("testLogout", "The logout should fail if there is no logged user"),ezshop.logout());
    }

    @Test
    public void testCreateProductType() {
        try {
            int id = ezshop.createProductType(productDescr1, barCode, pricePerUnit, note1);
            Assert.assertTrue(getErrorMsg("testCreateProductType", "The returned id should be a positive integer"), id > 0);
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductType", "The product description should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductType", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductType", "The product price should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductType", "The logged user should be authorized"));
        }
    }

    @Test
    public void testCreateProductTypeNullDescription() {
        try {
            int id = ezshop.createProductType(productDescr1, barCode, pricePerUnit, null);
            Assert.assertTrue(getErrorMsg("testCreateProductTypeNullDescription", "The returned id should be a positive integer"), id > 0);
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeNullDescription", "The product description should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeNullDescription", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeNullDescription", "The product price should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeNullDescription", "The logged user should be authorized"));
        }
    }

    @Test
    public void testCreateExistingProductType() {
        try {
            int id = ezshop.createProductType(productDescr1, barCode, pricePerUnit, note1);
            Assert.assertTrue(getErrorMsg("testCreateExistingProductType", "The returned id should be a positive integer"), id > 0);
            id = ezshop.createProductType("otherName", barCode, 10.0, note2);
            Assert.assertEquals(getErrorMsg("testCreateExistingProductType", "The returned id should be equal to -1"), -1, id);
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateExistingProductType", "The product description should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateExistingProductType", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateExistingProductType", "The product price should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateExistingProductType", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductDescriptionException.class)
    public void testCreateProductTypeNullType() throws InvalidProductDescriptionException {
        try {
            int id = ezshop.createProductType(null, barCode, pricePerUnit, note1);
            Assert.fail(getErrorMsg("testCreateProductTypeNullType", "Null is not a valid product description"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeNullType", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeNullType", "The product price should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeNullType", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductDescriptionException.class)
    public void testCreateProductTypeEmptyType() throws InvalidProductDescriptionException {
        try {
            int id = ezshop.createProductType("", barCode, pricePerUnit, note1);
            Assert.fail(getErrorMsg("testCreateProductTypeEmptyType", "Empty string is not a valid product type"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeEmptyType", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeEmptyType", "The product price should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeEmptyType", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductCodeException.class)
    public void testCreateProductTypeNullCode() throws InvalidProductCodeException {
        try {
            int id = ezshop.createProductType(productDescr1, null, pricePerUnit, note1);
            Assert.fail(getErrorMsg("testCreateProductTypeNullCode", "Null is not a valid product code"));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeNullCode", "The product description should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeNullCode", "The product price should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeNullCode", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductCodeException.class)
    public void testCreateProductTypeEmptyCode() throws InvalidProductCodeException {
        try {
            int id = ezshop.createProductType(productDescr1, "", pricePerUnit, note1);
            Assert.fail(getErrorMsg("testCreateProductTypeEmptyCode", "Empty string is not a valid product code"));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeEmptyCode", "The product description should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeEmptyCode", "The product price should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeEmptyCode", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductCodeException.class)
    public void testCreateProductTypeInvalidCode() throws InvalidProductCodeException {
        try {
            int id = ezshop.createProductType(productDescr1, invalidBarCode, pricePerUnit, note1);
            Assert.fail(getErrorMsg("testCreateProductTypeInvalidCode", "The product code should not be considered as valid"));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeInvalidCode", "The product description should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeInvalidCode", "The product price should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeInvalidCode", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidPricePerUnitException.class)
    public void testCreateProductTypeNegativePrice() throws InvalidPricePerUnitException {
        try {
            int id = ezshop.createProductType(productDescr1, barCode, -0.01, note1);
            Assert.fail(getErrorMsg("testCreateProductTypeNegativePrice", "The price should not be negative"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeNegativePrice", "The product code should not be considered as invalid"));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeNegativePrice", "The product description should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeNegativePrice", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidPricePerUnitException.class)
    public void testCreateProductTypeZeroPrice() throws InvalidPricePerUnitException {
        try {
            int id = ezshop.createProductType(productDescr1, barCode, 0.0, note1);
            Assert.fail(getErrorMsg("testCreateProductTypeZeroPrice", "The price should not be equal to zero"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeZeroPrice", "The product code should not be considered as invalid"));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeZeroPrice", "The product description should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeZeroPrice", "The logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testCreateProductTypeNotLogged() throws UnauthorizedException {
        try {
            ezshop.logout();
            int id = ezshop.createProductType(productDescr1, barCode, pricePerUnit, note1);
            Assert.fail(getErrorMsg("testCreateProductTypeNotLogged", "The operation should fail"));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeNotLogged", "The product description should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeNotLogged", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeNotLogged", "The product price should not be considered as invalid"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testCreateProductTypeNotAuthorized() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.createUser(username1,userPwd,cashier);
            ezshop.login(username1,userPwd);
            int id = ezshop.createProductType(productDescr1, barCode, pricePerUnit, note1);
            Assert.fail(getErrorMsg("testCreateProductTypeNotAuthorized", "The operation should fail"));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeNotAuthorized", "The product description should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeNotAuthorized", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeNotAuthorized", "The product price should not be considered as invalid"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeNotAuthorized", "The role should not be considered as invalid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeNotAuthorized", "The password should not be considered as invalid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeNotAuthorized", "The username should not be considered as invalid"));
        }
    }

    @Test
    public void testUpdateProduct() {
        try {
            int id = ezshop.createProductType(productDescr1, barCode, pricePerUnit, note1);
            Assert.assertTrue(getErrorMsg("testUpdateProduct", "The update should be successful"), ezshop.updateProduct(id, productDescr2, barCode, pricePerUnit, note1));
            Assert.assertTrue(getErrorMsg("testUpdateProduct", "The update should be successful"), ezshop.updateProduct(id, productDescr1, barCode2, pricePerUnit, note1));
            Assert.assertTrue(getErrorMsg("testUpdateProduct", "The update should be successful"), ezshop.updateProduct(id, productDescr1, barCode, pricePerUnit2, note1));
            Assert.assertTrue(getErrorMsg("testUpdateProduct", "The update should be successful"), ezshop.updateProduct(id, productDescr1, barCode, pricePerUnit, ""));
            Assert.assertTrue(getErrorMsg("testUpdateProduct", "The update should be successful"), ezshop.updateProduct(id, productDescr1, barCode, pricePerUnit, note2));
            Assert.assertTrue(getErrorMsg("testUpdateProduct", "The update should be successful"), ezshop.updateProduct(id, productDescr1, barCode, pricePerUnit, null));
            Assert.assertTrue(getErrorMsg("testUpdateProduct", "The update should be successful"), ezshop.updateProduct(id, productDescr2, barCode2, pricePerUnit2, note2));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProduct", "The product description should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProduct", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProduct", "The product price should not be considered as invalid"));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProduct", "The product id should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProduct", "The logged user should be authorized"));
        }
    }

    @Test
    public void testUpdateNotExistingProduct() {
        try {
            Assert.assertFalse(getErrorMsg("testUpdateNotExistingProduct", "The update should be successful"), ezshop.updateProduct(100, productDescr2, barCode, pricePerUnit, note1));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateNotExistingProduct", "The product description should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateNotExistingProduct", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateNotExistingProduct", "The product price should not be considered as invalid"));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateNotExistingProduct", "The product id should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateNotExistingProduct", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductIdException.class)
    public void testUpdateProductNegativeId() throws InvalidProductIdException {
        try {
            ezshop.updateProduct(-1, productDescr1, barCode, pricePerUnit, note1);
            Assert.fail(getErrorMsg("testUpdateProductNegativeId", "The product id should not be considered as valid"));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductNegativeId", "The product description should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductNegativeId", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductNegativeId", "The product price should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductNegativeId", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductIdException.class)
    public void testUpdateProductZeroId() throws InvalidProductIdException {
        try {
            ezshop.updateProduct(0, productDescr1, barCode, pricePerUnit, note1);
            Assert.fail(getErrorMsg("testUpdateProductZeroId", "The product id should not be considered as valid"));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductZeroId", "The product description should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductZeroId", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductZeroId", "The product price should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductZeroId", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductIdException.class)
    public void testUpdateProductNullId() throws InvalidProductIdException {
        try {
            ezshop.updateProduct(-1, productDescr1, barCode, pricePerUnit, note1);
            Assert.fail(getErrorMsg("testUpdateProductNullId", "The product id should not be considered as valid"));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductNullId", "The product description should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductNullId", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductNullId", "The product price should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductNullId", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductDescriptionException.class)
    public void testUpdateProductNullDescription() throws InvalidProductDescriptionException {
        try {
            ezshop.updateProduct(100, null, barCode, pricePerUnit, note1);
            Assert.fail(getErrorMsg("testUpdateProductNullType", "The product description should not be considered as valid"));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductNullType", "The product id should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductNullType", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductNullType", "The product price should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductNullType", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductDescriptionException.class)
    public void testUpdateProductEmptyDescription() throws InvalidProductDescriptionException {
        try {
            ezshop.updateProduct(100, "", barCode, pricePerUnit, note1);
            Assert.fail(getErrorMsg("testUpdateProductEmptyType", "The product description should not be considered as valid"));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductEmptyType", "The product id should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductEmptyType", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductEmptyType", "The product price should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductEmptyType", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductCodeException.class)
    public void testUpdateProductNullCode() throws InvalidProductCodeException {
        try {
            ezshop.updateProduct(100, productDescr1, null, pricePerUnit, note1);
            Assert.fail(getErrorMsg("testUpdateProductNullCode", "The product code should not be considered as valid"));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductNullCode", "The product id should not be considered as invalid"));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductNullCode", "The product description should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductNullCode", "The product price should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductNullCode", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductCodeException.class)
    public void testUpdateProductEmptyCode() throws InvalidProductCodeException {
        try {
            ezshop.updateProduct(100, productDescr1, "", pricePerUnit, note1);
            Assert.fail(getErrorMsg("testUpdateProductEmptyCode", "The product code should not be considered as valid"));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductEmptyCode", "The product id should not be considered as invalid"));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductEmptyCode", "The product description should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductEmptyCode", "The product price should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductEmptyCode", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductCodeException.class)
    public void testUpdateProductInvalidCode() throws InvalidProductCodeException {
        try {
            ezshop.updateProduct(100, productDescr1, invalidBarCode, pricePerUnit, note1);
            Assert.fail(getErrorMsg("testUpdateProductInvalidCode", "The product code should not be considered as valid"));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductInvalidCode", "The product id should not be considered as invalid"));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductInvalidCode", "The product description should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductInvalidCode", "The product price should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductInvalidCode", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidPricePerUnitException.class)
    public void testUpdateProductNegativePrice() throws InvalidPricePerUnitException {
        try {
            ezshop.updateProduct(100, productDescr1, barCode, -10.00, note1);
            Assert.fail(getErrorMsg("testUpdateProductNegativePrice", "The product price should not be considered as valid"));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductNegativePrice", "The product id should not be considered as invalid"));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductNegativePrice", "The product description should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductNegativePrice", "The product code should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductNegativePrice", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidPricePerUnitException.class)
    public void testUpdateProductZeroPrice() throws InvalidPricePerUnitException {
        try {
            ezshop.updateProduct(100, productDescr1, barCode, 0.00, note1);
            Assert.fail(getErrorMsg("testUpdateProductZeroPrice", "The product price should not be considered as valid"));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductZeroPrice", "The product id should not be considered as invalid"));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductZeroPrice", "The product description should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductZeroPrice", "The product code should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductZeroPrice", "The logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testUpdateProductNotLogged() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.updateProduct(100, productDescr1, barCode, pricePerUnit, note1);
            Assert.fail(getErrorMsg("testUpdateProductNotLogged", "The operation should fail"));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductNotLogged", "The product description should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductNotLogged", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductNotLogged", "The product price should not be considered as invalid"));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateProductNotLogged", "The product id should not be considered as invalid"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testUpdateProductNotAuthorized() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.createUser(username1,userPwd,cashier);
            ezshop.login(username1,userPwd);
            ezshop.updateProduct(100, productDescr1, barCode, pricePerUnit, note1);
            Assert.fail(getErrorMsg("testCreateProductTypeNotAuthorized", "The operation should fail"));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeNotAuthorized", "The product description should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeNotAuthorized", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeNotAuthorized", "The product price should not be considered as invalid"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeNotAuthorized", "The role should not be considered as invalid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeNotAuthorized", "The password should not be considered as invalid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeNotAuthorized", "The username should not be considered as invalid"));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateProductTypeNotAuthorized", "The product id should not be considered as invalid"));
        }
    }

    @Test
    public void testDeleteProductType() {
        try {
            int id = ezshop.createProductType(productDescr1, barCode, pricePerUnit, note1);
            Assert.assertTrue(getErrorMsg("testDeleteProductType", "The operation should not fail"), ezshop.deleteProductType(id));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductType", "The product description should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductType", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductType", "The product price should not be considered as invalid"));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductType", "The product id should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductType", "The logged user should be authorized"));
        }
    }

    @Test
    public void testDeleteNotExistingProductType() {
        try {
            Assert.assertFalse(getErrorMsg("testDeleteNotExistingProductType", "The operation should not fail"), ezshop.deleteProductType(100));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteNotExistingProductType", "The product id should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteNotExistingProductType", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductIdException.class)
    public void testDeleteProductTypeNegativeId() throws InvalidProductIdException {
        try {
            ezshop.deleteProductType(-10);
            Assert.fail(getErrorMsg("testDeleteProductTypeNegativeId", "The product id should not be considered as valid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductTypeNegativeId", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductIdException.class)
    public void testDeleteProductTypeZeroId() throws InvalidProductIdException {
        try {
            ezshop.deleteProductType(0);
            Assert.fail(getErrorMsg("testDeleteNotExistingZeroId", "The product id should not be considered as valid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteNotExistingZeroId", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductIdException.class)
    public void testDeleteProductTypeNullId() throws InvalidProductIdException {
        try {
            ezshop.deleteProductType(null);
            Assert.fail(getErrorMsg("testDeleteNotExistingNullId", "The product id should not be considered as valid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteNotExistingNullId", "The logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testDeleteProductTypeNotLogged() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.deleteProductType(100);
            Assert.fail(getErrorMsg("testDeleteProductTypeNotLogged", "The operation should fail"));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductTypeNotLogged", "The product id should not be considered as invalid"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testDeleteProductTypeNotAuthorized() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.createUser(username1,userPwd,cashier);
            ezshop.login(username1,userPwd);
            ezshop.deleteProductType(100);
            Assert.fail(getErrorMsg("testDeleteProductTypeNotAuthorized", "The operation should fail"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductTypeNotAuthorized", "The role should not be considered as invalid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductTypeNotAuthorized", "The password should not be considered as invalid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductTypeNotAuthorized", "The username should not be considered as invalid"));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductTypeNotAuthorized", "The product id should not be considered as invalid"));
        }
    }

    @Test
    public void testGetAllProductTypes() {
        try {
            Assert.assertEquals(getErrorMsg("testGetAllProductTypes", "There should be no product types"), 0, ezshop.getAllProductTypes().size());
            int id1 = ezshop.createProductType(productDescr1, barCode, pricePerUnit, note1);
            Assert.assertEquals(getErrorMsg("testGetAllProductTypes", "There should be 1 product type"), 1, ezshop.getAllProductTypes().size());
            List<ProductType> products = ezshop.getAllProductTypes();
            Assert.assertEquals(getErrorMsg("testGetAllProductTypes", "The product descriptions do not match"), productDescr1, products.get(0).getProductDescription());
            Assert.assertEquals(getErrorMsg("testGetAllProductTypes", "The product codes do not match"), barCode, products.get(0).getBarCode());
            Assert.assertEquals(getErrorMsg("testGetAllProductTypes", "The product prices do not match"), pricePerUnit, products.get(0).getPricePerUnit(),0.0);
            Assert.assertEquals(getErrorMsg("testGetAllProductTypes", "The product ids do not match"), id1, products.get(0).getId().intValue());
            Assert.assertEquals(getErrorMsg("testGetAllProductTypes", "The product notes do not match"), note1, products.get(0).getNote());

            int id2 = ezshop.createProductType(productDescr2, barCode2, pricePerUnit2, "");
            Assert.assertEquals(getErrorMsg("testGetAllProductTypes", "There should be 2 product types"), 2, ezshop.getAllProductTypes().size());
            products = ezshop.getAllProductTypes();

            boolean match1 = false;
            boolean match2 = false;
            for (ProductType t : products) {
                if (t.getId() == id1 && !match1) {
                    Assert.assertEquals(getErrorMsg("testGetAllProductTypes", "The product descriptions do not match"), productDescr1, t.getProductDescription());
                    Assert.assertEquals(getErrorMsg("testGetAllProductTypes", "The product codes do not match"), barCode, t.getBarCode());
                    Assert.assertEquals(getErrorMsg("testGetAllProductTypes", "The product prices do not match"), pricePerUnit, t.getPricePerUnit(),0.0);
                    Assert.assertEquals(getErrorMsg("testGetAllProductTypes", "The product notes do not match"), note1, t.getNote());
                    match1 = true;
                } else if (t.getId() == id2 && !match2) {
                    Assert.assertEquals(getErrorMsg("testGetAllProductTypes", "The product descriptions do not match"), productDescr2, t.getProductDescription());
                    Assert.assertEquals(getErrorMsg("testGetAllProductTypes", "The product codes do not match"), barCode2, t.getBarCode());
                    Assert.assertEquals(getErrorMsg("testGetAllProductTypes", "The product prices do not match"), pricePerUnit2, t.getPricePerUnit(),0.0);
                    Assert.assertEquals(getErrorMsg("testGetAllProductTypes", "The product notes do not match"), emptyNote, t.getNote());
                    match2 = true;
                } else break;
            }
            Assert.assertTrue(getErrorMsg("testGetAllProductTypes", "There should be both product types"), match1 && match2);

            Assert.assertTrue(getErrorMsg("testGetAllProductTypes", "The operation should not fail"), ezshop.deleteProductType(id1));
            products = ezshop.getAllProductTypes();
            Assert.assertEquals(getErrorMsg("testGetAllProductTypes", "There should be 1 product type"), 1, products.size());
            Assert.assertEquals(getErrorMsg("testGetAllProductTypes", "The product descriptions do not match"), productDescr2, products.get(0).getProductDescription());
            Assert.assertEquals(getErrorMsg("testGetAllProductTypes", "The product codes do not match"), barCode2, products.get(0).getBarCode());
            Assert.assertEquals(getErrorMsg("testGetAllProductTypes", "The product prices do not match"), pricePerUnit2, products.get(0).getPricePerUnit(),0.0);
            Assert.assertEquals(getErrorMsg("testGetAllProductTypes", "The product ids do not match"), id2, products.get(0).getId().intValue());
            Assert.assertEquals(getErrorMsg("testGetAllProductTypes", "The product notes do not match"), emptyNote, products.get(0).getNote());
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetAllProductTypes", "The product description should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetAllProductTypes", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetAllProductTypes", "The product price should not be considered as invalid"));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetAllProductTypes", "The product id should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetAllProductTypes", "The logged user should be authorized"));
        }
    }

    @Test
    public void testGetProductTypeByBarCode() {
        try {
            int id = ezshop.createProductType(productDescr1, barCode, pricePerUnit, note1);
            ProductType type = ezshop.getProductTypeByBarCode(barCode);
            Assert.assertNotNull(getErrorMsg("testGetProductTypeByBarCode", "There should be a product type with given bar code"), type);
            Assert.assertEquals(getErrorMsg("testGetProductTypeByBarCode", "The product description does not match"), productDescr1, type.getProductDescription());
            Assert.assertEquals(getErrorMsg("testGetProductTypeByBarCode", "The product code does not match"), barCode, type.getBarCode());
            Assert.assertEquals(getErrorMsg("testGetProductTypeByBarCode", "The product price does not match"), pricePerUnit, type.getPricePerUnit(),0.0);
            Assert.assertEquals(getErrorMsg("testGetProductTypeByBarCode", "The product id does not match"), id, type.getId().intValue());
            Assert.assertEquals(getErrorMsg("testGetProductTypeByBarCode", "The product note does not match"), note1, type.getNote());
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetProductTypeByBarCode", "The product description should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetProductTypeByBarCode", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetProductTypeByBarCode", "The product price should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetProductTypeByBarCode", "The logged user should be authorized"));
        }
    }

    @Test
    public void testGetNotExistingProductTypeByBarCode() {
        try {
            ProductType type = ezshop.getProductTypeByBarCode(barCode);
            Assert.assertNull(getErrorMsg("testGetNotExistingProductTypeByBarCode", "There shouldn't be a product type with given bar code"), type);
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetNotExistingProductTypeByBarCode", "The product code should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetNotExistingProductTypeByBarCode", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductCodeException.class)
    public void testGetProductTypeByBarCodeNull() throws InvalidProductCodeException {
        try {
            ezshop.getProductTypeByBarCode(null);
            Assert.fail(getErrorMsg("testGetProductTypeByBarCodeNull", "The bar code should be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetProductTypeByBarCodeNull", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductCodeException.class)
    public void testGetProductTypeByBarCodeEmpty() throws InvalidProductCodeException {
        try {
            ezshop.getProductTypeByBarCode("");
            Assert.fail(getErrorMsg("testGetProductTypeByBarCodeEmpty", "The bar code should be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetProductTypeByBarCodeEmpty", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductCodeException.class)
    public void testGetProductTypeByBarCodeInvalid() throws InvalidProductCodeException {
        try {
            ezshop.getProductTypeByBarCode(invalidBarCode);
            Assert.fail(getErrorMsg("testGetProductTypeByBarCodeInvalid", "The bar code should be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetProductTypeByBarCodeInvalid", "The logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetProductTypeByBarCodeNotLogged() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.getProductTypeByBarCode(barCode);
            Assert.fail(getErrorMsg("testGetProductTypeByBarCodeNotLogged", "The should have failed"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetProductTypeByBarCodeNotLogged", "The bar code should not be considered invalid"));
        }
    }

    @Test
    public void testGetProductTypesByDescription() {
        try {
            List<ProductType> products = ezshop.getProductTypesByDescription("");
            Assert.assertTrue(getErrorMsg("testGetProductTypesByDescription", "There should be no product types"), products.isEmpty());

            int id1 = ezshop.createProductType(productDescr1, barCode, pricePerUnit, note1);
            int id2 = ezshop.createProductType(productDescr2, barCode2, pricePerUnit2, note2);
            products = ezshop.getProductTypesByDescription("test");
            Assert.assertEquals(getErrorMsg("testGetProductTypesByDescription", "There should be 2 product types"), 2, products.size());
            products = ezshop.getProductTypesByDescription(productDescr1);
            Assert.assertEquals(getErrorMsg("testGetProductTypesByDescription", "There should be 1 product type"), 1, products.size());
            products = ezshop.getProductTypesByDescription("");
            Assert.assertEquals(getErrorMsg("testGetProductTypesByDescription", "There should be 2 product types"), 2, products.size());
            products = ezshop.getProductTypesByDescription(null);
            Assert.assertEquals(getErrorMsg("testGetProductTypesByDescription", "There should be 2 product types"), 2, products.size());
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetProductTypesByDescription", "The product description should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetProductTypesByDescription", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetProductTypesByDescription", "The product price should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetProductTypesByDescription", "The logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetProductTypesByDescriptionNotLogged() throws UnauthorizedException {
        ezshop.logout();
        ezshop.getProductTypesByDescription("");
        Assert.fail(getErrorMsg("testGetProductTypesByDescriptionNotLogged", "The operation should fail"));
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetProductTypesByDescriptionNotAuthorized() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.createUser(username1,userPwd,cashier);
            ezshop.login(username1,userPwd);
            ezshop.getProductTypesByDescription("");
            Assert.fail(getErrorMsg("testGetProductTypesByDescriptionNotAuthorized", "The operation should fail"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetProductTypesByDescriptionNotAuthorized", "The role should not be considered as invalid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetProductTypesByDescriptionNotAuthorized", "The password should not be considered as invalid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetProductTypesByDescriptionNotAuthorized", "The username should not be considered as invalid"));
        }
    }

    @Test
    public void testUpdateQuantity() {
        try {
            int id = ezshop.createProductType(productDescr1, barCode, pricePerUnit, note1);
            ezshop.updatePosition(id,location1);
            Assert.assertTrue(getErrorMsg("testUpdateQuantity", "The operation should succeed"), ezshop.updateQuantity(id, quantity));
            ProductType product = ezshop.getProductTypeByBarCode(barCode);
            Assert.assertEquals(getErrorMsg("testUpdateQuantity", "The quantity should be updated"), quantity, product.getQuantity().intValue());

            Assert.assertTrue(getErrorMsg("testUpdateQuantity", "The operation should succeed"), ezshop.updateQuantity(id, quantity));
            product = ezshop.getProductTypeByBarCode(barCode);
            Assert.assertEquals(getErrorMsg("testUpdateQuantity", "The quantity should be updated"), quantity + quantity, product.getQuantity().intValue());

            Assert.assertTrue(getErrorMsg("testUpdateQuantity", "The operation should succeed"), ezshop.updateQuantity(id, -quantity));
            product = ezshop.getProductTypeByBarCode(barCode);
            Assert.assertEquals(getErrorMsg("testUpdateQuantity", "The quantity should be updated"), quantity, product.getQuantity().intValue());
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateQuantity", "The product description should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateQuantity", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateQuantity", "The product price should not be considered as invalid"));
        }  catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateQuantity", "The product id should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateQuantity", "The logged user should be authorized"));
        } catch (InvalidLocationException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateQuantity", "The product location should not be considered invalid"));
        }
    }

    @Test
    public void testUpdateQuantityNotExisting() {
        try {
            Assert.assertFalse(getErrorMsg("testUpdateQuantityNotExisting", "The operation should not succeed"), ezshop.updateQuantity(100, quantity));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateQuantityNotExisting", "The product id should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateQuantityNotExisting", "The logged user should be authorized"));
        }
    }

    @Test
    public void testUpdateQuantityNoLocation() {
        try {
            int id = ezshop.createProductType(productDescr1, barCode, pricePerUnit, note1);
            Assert.assertFalse(getErrorMsg("testUpdateQuantityNotExisting", "The operation should not succeed"), ezshop.updateQuantity(100, quantity));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateQuantityNotExisting", "The product id should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateQuantityNotExisting", "The logged user should be authorized"));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateQuantityNotExisting", "The product description should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateQuantityNotExisting", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateQuantityNotExisting", "The product price should not be considered as invalid"));
        }
    }

    @Test
    public void testUpdateQuantityNegative() {
        try {
            int id = ezshop.createProductType(productDescr1, barCode, pricePerUnit, note1);
            ezshop.updatePosition(id,location1);
            Assert.assertTrue(getErrorMsg("testUpdateQuantityNegative", "The operation should succeed"), ezshop.updateQuantity(id, quantity));

            Assert.assertFalse(getErrorMsg("testUpdateQuantityNegative", "The operation should not succeed"), ezshop.updateQuantity(id, -(2 * quantity)));
            ProductType product = ezshop.getProductTypeByBarCode(barCode);
            Assert.assertEquals(getErrorMsg("testUpdateQuantityNegative", "The quantity should not be updated"), quantity, product.getQuantity().intValue());
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateQuantityNegative", "The product id should not be considered as invalid"));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateQuantityNegative", "The product description should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateQuantityNegative", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateQuantityNegative", "The product price should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateQuantityNegative", "The logged user should be authorized"));
        } catch (InvalidLocationException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateQuantity", "The product location should not be considered invalid"));
        }
    }

    @Test(expected = InvalidProductIdException.class)
    public void testUpdateQuantityNullId() throws InvalidProductIdException {
        try {
            ezshop.updateQuantity(null, quantity);
            Assert.fail(getErrorMsg("testUpdateQuantityNullId", "The operation should not succeed"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateQuantityNullId", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductIdException.class)
    public void testUpdateQuantityNegativeId() throws InvalidProductIdException {
        try {
            ezshop.updateQuantity(-1, quantity);
            Assert.fail(getErrorMsg("testUpdateQuantityNegativeId", "The operation should not succeed"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateQuantityNegativeId", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductIdException.class)
    public void testUpdateQuantityZeroId() throws InvalidProductIdException {
        try{
        ezshop.updateQuantity(0, quantity);
        Assert.fail(getErrorMsg("testUpdateQuantityZeroId", "The operation should not succeed"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateQuantityZeroId", "The logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testUpdateQuantityNotLogged() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.updateQuantity(100, quantity);
            Assert.fail(getErrorMsg("testUpdateQuantityNotLogged","The operation should fail"));
        }  catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateQuantityNotLogged", "The product id should not be considered as invalid"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testUpdateQuantityNotAuthorized() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.createUser(username1,userPwd,cashier);
            ezshop.login(username1,userPwd);
            ezshop.updateQuantity(100, quantity);
            Assert.fail(getErrorMsg("testUpdateQuantityNotLogged","The operation should fail"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateQuantityNotLogged", "The role should not be considered as invalid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateQuantityNotLogged", "The password should not be considered as invalid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateQuantityNotLogged", "The username should not be considered as invalid"));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdateQuantityNotLogged", "The product id should not be considered as invalid"));
        }
    }

    @Test(expected = InvalidProductIdException.class)
    public void testUpdatePositionNegative() throws InvalidProductIdException {
        try{
            ezshop.updatePosition(-1,location1);
            Assert.fail(getErrorMsg("testUpdatePositionNegative","The operation should have failed"));
        } catch (InvalidLocationException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdatePositionNegative", "The product location should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdatePositionNegative", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductIdException.class)
    public void testUpdatePositionZero() throws InvalidProductIdException {
        try{
            ezshop.updatePosition(0,location1);
            Assert.fail(getErrorMsg("testUpdatePositionZero","The operation should have failed"));
        } catch (InvalidLocationException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdatePositionZero", "The product location should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdatePositionZero", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductIdException.class)
    public void testUpdatePositionNull() throws InvalidProductIdException {
        try{
            ezshop.updatePosition(null,location1);
            Assert.fail(getErrorMsg("testUpdatePositionNull","The operation should have failed"));
        } catch (InvalidLocationException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdatePositionNull", "The product location should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdatePositionNull", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidLocationException.class)
    public void testUpdatePositionInvalidLocation1() throws InvalidLocationException {
        try{
            ezshop.updatePosition(100,invalidLocation1);
            Assert.fail(getErrorMsg("testUpdatePositionInvalidLocation","The operation should have failed"));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdatePositionInvalidLocation", "The product id should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdatePositionInvalidLocation", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidLocationException.class)
    public void testUpdatePositionInvalidLocation2() throws InvalidLocationException {
        try{
            ezshop.updatePosition(100,invalidLocation2);
            Assert.fail(getErrorMsg("testUpdatePositionInvalidLocation","The operation should have failed"));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdatePositionInvalidLocation", "The product id should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdatePositionInvalidLocation", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidLocationException.class)
    public void testUpdatePositionInvalidLocation3() throws InvalidLocationException {
        try{
            ezshop.updatePosition(100,invalidLocation3);
            Assert.fail(getErrorMsg("testUpdatePositionInvalidLocation","The operation should have failed"));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdatePositionInvalidLocation", "The product id should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdatePositionInvalidLocation", "The logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testUpdatePositionNotLogged() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.updatePosition(100, location1);
            Assert.fail(getErrorMsg("testUpdatePositionNotLogged","The operation should fail"));
        }  catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdatePositionNotLogged", "The product id should not be considered as invalid"));
        } catch (InvalidLocationException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdatePositionNotLogged", "The product location should not be considered as invalid"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testUpdatePositionNotAuthorized() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.createUser(username1,userPwd,cashier);
            ezshop.login(username1,userPwd);
            ezshop.updatePosition(100, location1);
            Assert.fail(getErrorMsg("testUpdatePositionNotAuthorized","The operation should fail"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdatePositionNotAuthorized", "The role should not be considered as invalid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdatePositionNotAuthorized", "The password should not be considered as invalid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdatePositionNotAuthorized", "The username should not be considered as invalid"));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdatePositionNotAuthorized", "The product id should not be considered as invalid"));
        } catch (InvalidLocationException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testUpdatePositionNotAuthorized", "The product location should not be considered as invalid"));
        }
    }

    @Test
    public void testIssueReorderWarning() {
        try {
            int id = ezshop.createProductType(productDescr1, barCode, pricePerUnit, note1);
            int orderId = ezshop.issueOrder(barCode, quantity, orderPricePerUnit);
            Assert.assertTrue(getErrorMsg("testIssueReorderWarning", "The operation should not fail"), orderId > 0);
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarning", "The product description should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarning", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarning", "The product price should not be considered as invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarning", "The product quantity should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarning", "The logged user should be authorized"));
        }
    }

    @Test
    public void testIssueReorderWarningNotExisting() {
        try {
            Assert.assertFalse(getErrorMsg("testIssueReorderWarningNotExisting", "The operation should fail"), ezshop.issueOrder(barCode, quantity, orderPricePerUnit) > 0);
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningNotExisting", "The product code should not be considered as invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningNotExisting", "The product quantity should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningNotExisting", "The product price should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningNotExisting", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductCodeException.class)
    public void testIssueReorderWarningEmpty() throws InvalidProductCodeException {
        try {
            ezshop.issueOrder("", quantity,orderPricePerUnit);
            Assert.fail(getErrorMsg("testIssueReorderWarningEmpty", "The product code should be considered as invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningEmpty", "The product quantity should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningEmpty", "The product price should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningEmpty", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductCodeException.class)
    public void testIssueReorderWarningNull() throws InvalidProductCodeException {
        try {
            ezshop.issueOrder(null, quantity,orderPricePerUnit);
            Assert.fail(getErrorMsg("testIssueReorderWarningNull", "The product code should be considered as invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningNull", "The product quantity should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningNull", "The product price should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningNull", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductCodeException.class)
    public void testIssueReorderWarningInvalid() throws InvalidProductCodeException {
        try {
            ezshop.issueOrder(invalidBarCode, quantity,orderPricePerUnit);
            Assert.fail(getErrorMsg("testIssueReorderWarningInvalid", "The product code should be considered as invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningInvalid", "The product quantity should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningInvalid", "The product price should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningInvalid", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidQuantityException.class)
    public void testIssueReorderWarningNegativeQuantity() throws InvalidQuantityException {
        try {
            ezshop.issueOrder(barCode, -1,orderPricePerUnit);
            Assert.fail(getErrorMsg("testIssueReorderWarningNegativeQuantity", "The product quantity should be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningNegativeQuantity", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningNegativeQuantity", "The product price should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningNegativeQuantity", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidQuantityException.class)
    public void testIssueReorderWarningZeroQuantity() throws InvalidQuantityException {
        try {
            ezshop.issueOrder(barCode, 0,orderPricePerUnit);
            Assert.fail(getErrorMsg("testIssueReorderWarningZeroQuantity", "The product quantity should be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningZeroQuantity", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningZeroQuantity", "The product price should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningZeroQuantity", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidPricePerUnitException.class)
    public void testIssueReorderWarningNegativePrice() throws InvalidPricePerUnitException {
        try {
            ezshop.issueOrder(barCode, quantity,-orderPricePerUnit);
            Assert.fail(getErrorMsg("testIssueReorderWarningNegativePrice", "The product price should be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningNegativePrice", "The product code should not be considered as invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningNegativePrice", "The product quantity should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningNegativePrice", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidPricePerUnitException.class)
    public void testIssueReorderWarningZeroPrice() throws InvalidPricePerUnitException {
        try {
            ezshop.issueOrder(barCode, quantity,0);
            Assert.fail(getErrorMsg("testIssueReorderWarningZeroPrice", "The product price should be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningZeroPrice", "The product code should not be considered as invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningZeroPrice", "The product quantity should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningZeroPrice", "The logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testIssueReorderWarningNotLogged() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.issueOrder(barCode,quantity,pricePerUnit);
            Assert.fail(getErrorMsg("testIssueReorderWarningNotLogged","The operation should fail"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningNotLogged", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningNotLogged", "The product price should not be considered as invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningNotLogged", "The product quantity should not be considered as invalid"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testIssueReorderWarningNotAuthorized() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.createUser(username1,userPwd,cashier);
            ezshop.login(username1,userPwd);
            ezshop.issueOrder(barCode,quantity,pricePerUnit);
            Assert.fail(getErrorMsg("testIssueReorderWarningNotAuthorized","The operation should fail"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningNotAuthorized", "The role should not be considered as invalid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningNotAuthorized", "The password should not be considered as invalid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningNotAuthorized", "The username should not be considered as invalid"));
        }  catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningNotAuthorized", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningNotAuthorized", "The product price should not be considered as invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarningNotAuthorized", "The product quantity should not be considered as invalid"));
        }
    }

    @Test
    public void testSendOrderFor(){
        try {
            ezshop.recordBalanceUpdate(pricePerUnit*quantity);
            int id = ezshop.createProductType(productDescr1, barCode, pricePerUnit, note1);
            Assert.assertTrue(getErrorMsg("testSendOrderFor", "The operation should not fail"), ezshop.payOrderFor(barCode, quantity, orderPricePerUnit) > 0);
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderFor", "The product description should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderFor", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderFor", "The product price should not be considered as invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderFor", "The product quantity should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderFor", "The logged user should be authorized"));
        }
    }

    @Test
    public void testSendOrderForNotExisting(){
        try {
            ezshop.recordBalanceUpdate(pricePerUnit*quantity);
            Assert.assertFalse(getErrorMsg("testSendOrderForNotExisting", "The operation should fail"), ezshop.payOrderFor(barCode, quantity, orderPricePerUnit) > 0);
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForNotExisting", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForNotExisting", "The product price should not be considered as invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForNotExisting", "The product quantity should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForNotExisting", "The logged user should be authorized"));
        }
    }

    @Test
    public void testSendOrderForNotEnoughMoney(){
        try {
            Assert.assertFalse(getErrorMsg("testSendOrderForNotEnoughMoney", "The operation should fail"), ezshop.payOrderFor(barCode, quantity, orderPricePerUnit) > 0);
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForNotEnoughMoney", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForNotEnoughMoney", "The product price should not be considered as invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForNotEnoughMoney", "The product quantity should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForNotEnoughMoney", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductCodeException.class)
    public void testSendOrderForEmptyBarCode() throws InvalidProductCodeException {
        try {
            ezshop.recordBalanceUpdate(pricePerUnit*quantity);
            ezshop.payOrderFor("", quantity, orderPricePerUnit);
            Assert.fail(getErrorMsg("testIssueReorderWarning", "The operation should fail"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarning", "The product price should not be considered as invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarning", "The product quantity should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testIssueReorderWarning", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductCodeException.class)
    public void testSendOrderForNullBarCode() throws InvalidProductCodeException {
        try {
            ezshop.recordBalanceUpdate(pricePerUnit*quantity);
            ezshop.payOrderFor(null, quantity, orderPricePerUnit);
            Assert.fail(getErrorMsg("testSendOrderForNullBarCode", "The operation should fail"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForNullBarCode", "The product price should not be considered as invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForNullBarCode", "The product quantity should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForNullBarCode", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductCodeException.class)
    public void testSendOrderForInvalidBarCode() throws InvalidProductCodeException {
        try {
            ezshop.recordBalanceUpdate(pricePerUnit*quantity);
            ezshop.payOrderFor(invalidBarCode, quantity, orderPricePerUnit);
            Assert.fail(getErrorMsg("testSendOrderForInvalidBarCode", "The operation should fail"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForInvalidBarCode", "The product price should not be considered as invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForInvalidBarCode", "The product quantity should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForInvalidBarCode", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidQuantityException.class)
    public void testSendOrderForNegativeQuantity() throws InvalidQuantityException {
        try {
            ezshop.recordBalanceUpdate(pricePerUnit*quantity);
            ezshop.payOrderFor(barCode, -quantity, orderPricePerUnit);
            Assert.fail(getErrorMsg("testSendOrderForNegativeQuantity", "The operation should fail"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForNegativeQuantity", "The product price should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForNegativeQuantity", "The product code should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForNegativeQuantity", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidQuantityException.class)
    public void testSendOrderForZeroQuantity() throws InvalidQuantityException {
        try {
            ezshop.recordBalanceUpdate(pricePerUnit*quantity);
            ezshop.payOrderFor(barCode, 0, orderPricePerUnit);
            Assert.fail(getErrorMsg("testSendOrderForZeroQuantity", "The operation should fail"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForZeroQuantity", "The product price should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForZeroQuantity", "The product code should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForZeroQuantity", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidPricePerUnitException.class)
    public void testSendOrderForNegativePrice() throws InvalidPricePerUnitException {
        try {
            ezshop.recordBalanceUpdate(pricePerUnit*quantity);
            ezshop.payOrderFor(barCode, quantity, -orderPricePerUnit);
            Assert.fail(getErrorMsg("testSendOrderForNegativePrice", "The operation should fail"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForNegativePrice", "The product quantity should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForNegativePrice", "The product code should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForNegativePrice", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidPricePerUnitException.class)
    public void testSendOrderForZeroPrice() throws InvalidPricePerUnitException {
        try {
            ezshop.recordBalanceUpdate(pricePerUnit*quantity);
            ezshop.payOrderFor(barCode, quantity, 0);
            Assert.fail(getErrorMsg("testSendOrderForZeroPrice", "The operation should fail"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForZeroPrice", "The product quantity should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForZeroPrice", "The product code should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForZeroPrice", "The logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testSendOrderForNotLogged() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.payOrderFor(barCode,quantity,pricePerUnit);
            Assert.fail(getErrorMsg("testSendOrderForNotLogged","The operation should fail"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForNotLogged", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForNotLogged", "The product price should not be considered as invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForNotLogged", "The product quantity should not be considered as invalid"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testSendOrderForNotAuthorized() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.createUser(username1,userPwd,cashier);
            ezshop.login(username1,userPwd);
            ezshop.payOrderFor(barCode,quantity,pricePerUnit);
            Assert.fail(getErrorMsg("testSendOrderForNotAuthorized","The operation should fail"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForNotAuthorized", "The role should not be considered as invalid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForNotAuthorized", "The password should not be considered as invalid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForNotAuthorized", "The username should not be considered as invalid"));
        }  catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForNotAuthorized", "The product code should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForNotAuthorized", "The product price should not be considered as invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderForNotAuthorized", "The product quantity should not be considered as invalid"));
        }
    }

    @Test
    public void testSendOrder(){
        try{
            ezshop.recordBalanceUpdate(pricePerUnit*quantity);
            int id = ezshop.createProductType(productDescr1, barCode, pricePerUnit, note1);
            int orderId = ezshop.issueOrder(barCode, quantity, orderPricePerUnit);
            Assert.assertTrue(getErrorMsg("testSendOrder", "The operation should not fail"),ezshop.payOrder(orderId));
        } catch (InvalidOrderIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrder", "The order id should not be considered as invalid"));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrder", "The product description should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrder", "The product price should not be considered as invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrder", "The product quantity should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrder", "The product code should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrder", "The logged user should be authorized"));
        }
    }

    @Test
    public void testSendOrderNotEnoughMoney(){
        try{
            int id = ezshop.createProductType(productDescr1, barCode, pricePerUnit, note1);
            int orderId = ezshop.issueOrder(barCode, quantity, orderPricePerUnit);
            Assert.assertFalse(getErrorMsg("testSendOrderNotEnoughMoney", "The operation should fail"),ezshop.payOrder(orderId));
        } catch (InvalidOrderIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderNotEnoughMoney", "The order id should not be considered as invalid"));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderNotEnoughMoney", "The product description should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderNotEnoughMoney", "The product price should not be considered as invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderNotEnoughMoney", "The product quantity should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderNotEnoughMoney", "The product code should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderNotEnoughMoney", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidOrderIdException.class)
    public void testSendOrderNullId() throws InvalidOrderIdException {
        try{
            ezshop.payOrder(null);
            Assert.fail(getErrorMsg("testSendOrderNullId", "The operation should fail"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderNullId", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidOrderIdException.class)
    public void testSendOrderNegativeId() throws InvalidOrderIdException {
        try {
            ezshop.payOrder(-1);
            Assert.fail(getErrorMsg("testSendOrderNegativeId", "The operation should fail"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderNegativeId", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidOrderIdException.class)
    public void testSendOrderZeroId() throws InvalidOrderIdException {
        try {
            ezshop.payOrder(null);
            Assert.fail(getErrorMsg("testSendOrderZeroId", "The operation should fail"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderZeroId", "The logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testSendOrderNotLogged() throws UnauthorizedException {
        try{
            ezshop.logout();
            ezshop.payOrder(100);
            Assert.fail(getErrorMsg("testSendOrderNotLogged", "The operation should have failed"));
        } catch (InvalidOrderIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderNotLogged", "The order id should not be considered as invalid"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testSendOrderNotAuthorized() throws UnauthorizedException {
        try{
            ezshop.logout();
            ezshop.createUser(username1,userPwd,cashier);
            ezshop.login(username1,userPwd);
            ezshop.payOrder(100);
            Assert.fail(getErrorMsg("testSendOrderNotAuthorized", "The operation should have failed"));
        } catch (InvalidOrderIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderNotAuthorized", "The order id should not be considered as invalid"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderNotAuthorized", "The role should not be considered as invalid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderNotAuthorized", "The password should not be considered as invalid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testSendOrderNotAuthorized", "The username should not be considered as invalid"));
        }
    }

    @Test
    public void testRecordOrderArrival(){
        try{
            ezshop.recordBalanceUpdate(pricePerUnit*quantity);
            int id = ezshop.createProductType(productDescr1, barCode, pricePerUnit, note1);
            ezshop.updatePosition(id,location1);
            int orderId = ezshop.issueOrder(barCode, quantity, orderPricePerUnit);
            ezshop.payOrder(orderId);
            Assert.assertTrue(getErrorMsg("testRecordOrderArrival", "The operation should not fail"),ezshop.recordOrderArrival(orderId));
        } catch (InvalidOrderIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderArrival", "The order id should not be considered as invalid"));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderArrival", "The product description should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderArrival", "The product price should not be considered as invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderArrival", "The product quantity should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderArrival", "The product code should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderArrival", "The logged user should be authorized"));
        } catch (InvalidLocationException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderArrival", "The product location should not be considered as invalid"));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderArrival", "The product id should not be considered as invalid"));
        }
    }

    @Test
    public void testRecordOrderArrivalNotExisting(){
        try{
            Assert.assertFalse(getErrorMsg("testRecordOrderArrivalNotExisting", "The operation should fail"),ezshop.recordOrderArrival(100));
        } catch (InvalidOrderIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderArrivalNotExisting", "The order id should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderArrivalNotExisting", "The logged user should be authorized"));
        } catch (InvalidLocationException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderArrivalNotExisting", "The product location should not be considered as invalid"));
        }
    }

    @Test(expected = InvalidOrderIdException.class)
    public void testRecordOrderArrivalNegative() throws InvalidOrderIdException {
        try {
            ezshop.recordOrderArrival(-1);
            Assert.fail(getErrorMsg("testRecordOrderArrivalNegative", "The order id should be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderArrivalNegative", "The logged user should be authorized"));
        } catch (InvalidLocationException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderArrivalNegative", "The product location should not be considered as invalid"));
        }
    }

    @Test(expected = InvalidOrderIdException.class)
    public void testRecordOrderArrivalZero() throws InvalidOrderIdException {
        try {
            ezshop.recordOrderArrival(0);
            Assert.fail(getErrorMsg("testRecordOrderArrivalZero", "The order id should be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderArrivalZero", "The logged user should be authorized"));
        } catch (InvalidLocationException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderArrivalZero", "The product location should not be considered as invalid"));
        }
    }

    @Test(expected = InvalidOrderIdException.class)
    public void testRecordOrderArrivalNull() throws InvalidOrderIdException {
        try {
            ezshop.recordOrderArrival(null);
            Assert.fail(getErrorMsg("testRecordOrderArrivalNull", "The order id should be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderArrivalNull", "The logged user should be authorized"));
        } catch (InvalidLocationException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderArrivalNull", "The product location should not be considered as invalid"));
        }
    }

    @Test(expected = InvalidLocationException.class)
    public void testRecordOrderArrivalNoLoc() throws InvalidLocationException {
        try{
            ezshop.recordBalanceUpdate(pricePerUnit*quantity);
            int id = ezshop.createProductType(productDescr1, barCode, pricePerUnit, note1);
            int orderId = ezshop.issueOrder(barCode, quantity, orderPricePerUnit);
            ezshop.payOrder(orderId);
            ezshop.recordOrderArrival(orderId);
            Assert.fail(getErrorMsg("testRecordOrderArrivalNoLoc", "The operation should fail"));
        } catch (InvalidOrderIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderArrivalNoLoc", "The order id should not be considered as invalid"));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderArrivalNoLoc", "The product type should not be considered as invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderArrivalNoLoc", "The product price should not be considered as invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderArrivalNoLoc", "The product quantity should not be considered as invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderArrivalNoLoc", "The product code should not be considered as invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderArrivalNoLoc", "The logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testRecordOrderNotLogged() throws UnauthorizedException {
        try{
            ezshop.logout();
            ezshop.recordOrderArrival(100);
            Assert.fail(getErrorMsg("testRecordOrderNotLogged", "The operation should have failed"));
        } catch (InvalidOrderIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderNotLogged", "The order id should not be considered as invalid"));
        } catch (InvalidLocationException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderNotLogged", "The product location should not be considered as invalid"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testRecordOrderNotAuthorized() throws UnauthorizedException {
        try{
            ezshop.logout();
            ezshop.createUser(username1,userPwd,cashier);
            ezshop.login(username1,userPwd);
            ezshop.recordOrderArrival(100);
            Assert.fail(getErrorMsg("testRecordOrderNotAuthorized", "The operation should have failed"));
        } catch (InvalidOrderIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderNotAuthorized", "The order id should not be considered as invalid"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderNotAuthorized", "The role should not be considered as invalid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderNotAuthorized", "The password should not be considered as invalid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderNotAuthorized", "The username should not be considered as invalid"));
        } catch (InvalidLocationException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordOrderNotAuthorized", "The product location should not be considered as invalid"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetAllOrdersNotLogged() throws UnauthorizedException {
        ezshop.logout();
        ezshop.getAllOrders();
        Assert.fail(getErrorMsg("testGetAllOrdersNotLogged", "The operation should have failed"));
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetAllOrdersNotAuthorized() throws UnauthorizedException {
        try{
            ezshop.logout();
            ezshop.createUser(username1,userPwd,cashier);
            ezshop.login(username1,userPwd);
            ezshop.getAllOrders();
            Assert.fail(getErrorMsg("testGetAllOrdersNotAuthorized", "The operation should have failed"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetAllOrdersNotAuthorized", "The role should not be considered as invalid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetAllOrdersNotAuthorized", "The password should not be considered as invalid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetAllOrdersNotAuthorized", "The username should not be considered as invalid"));
        }
    }

    @Test
    public void testDefineCustomer() {
        try {
            int id = ezshop.defineCustomer(customerName1);
            Assert.assertTrue(getErrorMsg("testDefineCustomer", "The id should be > 0"), id > 0);
        } catch (InvalidCustomerNameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDefineCustomer", "The customer name should not be considered as invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDefineCustomer", "Logged user should be authorized"));
        }
    }

    @Test
    public void testDefineExistingCustomer() {
        try {
            int id = ezshop.defineCustomer(customerName1);
            Assert.assertTrue(getErrorMsg("testDefineExistingCustomer", "The id should be > 0"), id > 0);
            int id2 = ezshop.defineCustomer(customerName1);
            Assert.assertNotEquals(getErrorMsg("testDefineExistingCustomer", "The ids should not be equals"), id2, id);
        } catch (InvalidCustomerNameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDefineExistingCustomer", "The customer name should not be considered as invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDefineExistingCustomer", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidCustomerNameException.class)
    public void testDefineCustomerNullName() throws InvalidCustomerNameException {
        try {
            ezshop.defineCustomer(null);
            Assert.fail(getErrorMsg("testDefineCustomerNullName", "The operation should fail"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDefineCustomerNullName", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidCustomerNameException.class)
    public void testDefineCustomerEmptyName() throws InvalidCustomerNameException {
        try {
            ezshop.defineCustomer("");
            Assert.fail(getErrorMsg("testDefineCustomerEmptyName", "The operation should fail"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDefineCustomerEmptyName", "Logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testDefineCustomerNotLogged() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.defineCustomer(customerName1);
            Assert.fail(getErrorMsg("testDefineCustomerNotLogged", "The operation should fail"));
        } catch (InvalidCustomerNameException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDefineCustomerNotLogged", "Customer name should not be considered invalid"));
        }
    }

    @Test(expected = InvalidCustomerIdException.class)
    public void testModifyCustomerNegativeId() throws InvalidCustomerIdException {
        try {
            ezshop.modifyCustomer(-1, customerName1, customerCard1);
            Assert.fail(getErrorMsg("testModifyCustomerNegativeId", "The operation should fail"));
        } catch (InvalidCustomerCardException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testModifyCustomerNegativeId", "Customer card should not be considered as invalid"));
        } catch (InvalidCustomerNameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testModifyCustomerNegativeId", "Customer name should not be considered as invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testModifyCustomerNegativeId", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidCustomerIdException.class)
    public void testModifyCustomerNullId() throws InvalidCustomerIdException {
        try {
            ezshop.modifyCustomer(null, customerName1, customerCard1);
            Assert.fail(getErrorMsg("testModifyCustomerNullId", "The operation should fail"));
        } catch (InvalidCustomerCardException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testModifyCustomerNullId", "Customer card should not be considered as invalid"));
        } catch (InvalidCustomerNameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testModifyCustomerNullId", "Customer name should not be considered as invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testModifyCustomerNullId", "Logged user should be authorized"));
        }
    }


    @Test(expected = InvalidCustomerNameException.class)
    public void testModifyCustomerEmptyName() throws InvalidCustomerNameException {
        try {
            ezshop.modifyCustomer(100, "", customerCard1);
            Assert.fail(getErrorMsg("testModifyCustomerEmptyName", "The operation should fail"));
        } catch (InvalidCustomerCardException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testModifyCustomerEmptyName", "Customer card should not be considered as invalid"));
        } catch (InvalidCustomerIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testModifyCustomerEmptyName", "Customer id should not be considered as invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testModifyCustomerEmptyName", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidCustomerNameException.class)
    public void testModifyCustomerNullName() throws InvalidCustomerNameException {
        try {
            ezshop.modifyCustomer(100, null, customerCard1);
            Assert.fail(getErrorMsg("testModifyCustomerNullName", "The operation should fail"));
        } catch (InvalidCustomerCardException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testModifyCustomerNullName", "Customer card should not be considered as invalid"));
        } catch (InvalidCustomerIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testModifyCustomerNullName", "Customer id should not be considered as invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testModifyCustomerNullName", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidCustomerCardException.class)
    public void testModifyCustomerInvalidCard() throws InvalidCustomerCardException {
        try {
            ezshop.modifyCustomer(100, customerName1, invalidCustomerCard);
            Assert.fail(getErrorMsg("testModifyCustomerInvalidCard", "The operation should fail"));
        } catch (InvalidCustomerNameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testModifyCustomerInvalidCard", "Customer name should not be considered as invalid"));
        } catch (InvalidCustomerIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testModifyCustomerInvalidCard", "Customer id should not be considered as invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testModifyCustomerInvalidCard", "Logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testModifyCustomerNotLogged() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.modifyCustomer(100, customerName1, customerCard1);
            Assert.fail(getErrorMsg("testModifyCustomerNotLogged", "The operation should fail"));
        } catch (InvalidCustomerNameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testModifyCustomerNotLogged", "Customer name should not be considered as invalid"));
        } catch (InvalidCustomerIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testModifyCustomerNotLogged", "Customer id should not be considered as invalid"));
        } catch (InvalidCustomerCardException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testModifyCustomerNotLogged", "Customer card should not be considered as invalid"));
        }
    }

    @Test
    public void testDeleteCustomer() {
        try {
            int id = ezshop.defineCustomer(customerName1);
            Assert.assertTrue(getErrorMsg("testDeleteCustomer", "The operation should not fail"), ezshop.deleteCustomer(id));
        } catch (InvalidCustomerIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteCustomer", "Customer id should not be considered as invalid"));
        } catch (InvalidCustomerNameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteCustomer", "Customer name should not be considered as invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteCustomer", "Logged user should be authorized"));
        }
    }

    @Test
    public void testDeleteNotExistingCustomer() {
        try {
            Assert.assertFalse(getErrorMsg("testDeleteCustomer", "The operation should not fail"), ezshop.deleteCustomer(100));
        } catch (InvalidCustomerIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteCustomer", "Customer id should not be considered as invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteCustomer", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidCustomerIdException.class)
    public void testDeleteCustomerNegativeId() throws InvalidCustomerIdException {
        try {
            ezshop.deleteCustomer(-1);
            Assert.fail(getErrorMsg("testDeleteCustomerNegativeId", "The operation should fail"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteCustomerNegativeId", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidCustomerIdException.class)
    public void testDeleteCustomerZeroId() throws InvalidCustomerIdException {
        try {
            ezshop.deleteCustomer(0);
            Assert.fail(getErrorMsg("testDeleteCustomerZeroId", "The operation should fail"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteCustomerZeroId", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidCustomerIdException.class)
    public void testDeleteCustomerNullId() throws InvalidCustomerIdException {
        try {
            ezshop.deleteCustomer(null);
            Assert.fail(getErrorMsg("testDeleteCustomerNullId", "The operation should fail"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteCustomerNegativeId", "Logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testDeleteCustomerNotLogged() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.deleteCustomer(100);
            Assert.fail(getErrorMsg("testDeleteCustomerNotLogged", "The operation should fail"));
        } catch (InvalidCustomerIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteCustomerNotLogged", "Customer id should not be considered as invalid"));
        }
    }

    @Test
    public void testGetNotExistingCustomer() {
        try {
            Customer customer = ezshop.getCustomer(100);
            Assert.assertNull(getErrorMsg("testGetNotExistingCustomer", "The returned value should be null"), customer);
        } catch (InvalidCustomerIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetNotExistingCustomer", "Customer id should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetCustomer", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidCustomerIdException.class)
    public void testGetCustomerNegativeId() throws InvalidCustomerIdException {
        try {
            ezshop.getCustomer(-1);
            Assert.fail(getErrorMsg("testGetCustomerNegativeId", "The operation should fail"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetCustomerNegativeId", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidCustomerIdException.class)
    public void testGetCustomerZeroId() throws InvalidCustomerIdException {
        try {
            ezshop.getCustomer(0);
            Assert.fail(getErrorMsg("testGetCustomerZeroId", "The operation should fail"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetCustomerZeroId", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidCustomerIdException.class)
    public void testGetCustomerNullId() throws InvalidCustomerIdException {
        try {
            ezshop.getCustomer(null);
            Assert.fail(getErrorMsg("testGetCustomerNullId", "The operation should fail"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetCustomerNullId", "Logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetCustomerNotLogged() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.getCustomer(100);
            Assert.fail(getErrorMsg("testGetCustomerNotLogged", "The operation should fail"));
        } catch (InvalidCustomerIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testGetCustomerNotLogged", "Customer id should not be considered as invalid"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetAllCustomersNotLogged() throws UnauthorizedException {
        ezshop.logout();
        ezshop.getAllCustomers();
        Assert.fail(getErrorMsg("testGetAllCustomersNotLogged", "The operation should fail"));
    }

    @Test
    public void testCreateCard() {
        try {
            String card = ezshop.createCard();
            Assert.assertTrue(getErrorMsg("testCreateCard", "The card has not a valid code"), isValidCustomerCard(card));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCreateCard", "Logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testCreateCardNotLogged() throws UnauthorizedException {
        ezshop.logout();
        ezshop.createCard();
        Assert.fail(getErrorMsg("testCreateCardNotLogged", "The operation should fail"));
    }

    @Test
    public void testAttachCardToNonExistingCustomer() {
        try {
            Assert.assertFalse(getErrorMsg("testAttachCardToNonExistingCustomer", "This operation should fail"), ezshop.attachCardToCustomer(customerCard1, 100));
        } catch (InvalidCustomerIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAttachCardToNonExistingCustomer", "Customer id should not be considered invalid"));
        } catch (InvalidCustomerCardException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAttachCardToNonExistingCustomer", "Customer card should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAttachCardToNonExistingCustomer", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidCustomerCardException.class)
    public void testAttachEmptyCardToCustomer() throws InvalidCustomerCardException {
        try {
            ezshop.attachCardToCustomer("", 100);
            Assert.fail(getErrorMsg("testAttachEmptyCardToCustomer", "The operation should fail"));
        } catch (InvalidCustomerIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAttachEmptyCardToCustomer", "Customer id should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAttachEmptyCardToCustomer", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidCustomerCardException.class)
    public void testAttachNullCardToCustomer() throws InvalidCustomerCardException {
        try {
            ezshop.attachCardToCustomer(null, 100);
            Assert.fail(getErrorMsg("testAttachNullCardToCustomer", "The operation should fail"));
        } catch (InvalidCustomerIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAttachNullCardToCustomer", "Customer id should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAttachNullCardToCustomer", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidCustomerCardException.class)
    public void testAttachInvalidCardToCustomer() throws InvalidCustomerCardException {
        try {
            ezshop.attachCardToCustomer(null, 100);
            Assert.fail(getErrorMsg("testAttachInvalidCardToCustomer", "The operation should fail"));
        } catch (InvalidCustomerIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAttachInvalidCardToCustomer", "Customer id should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAttachInvalidCardToCustomer", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidCustomerIdException.class)
    public void testAttachCardToCustomerNegativeId() throws InvalidCustomerIdException {
        try {
            ezshop.attachCardToCustomer(customerCard1, -1);
            Assert.fail(getErrorMsg("testAttachCardToCustomerNegativeId", "The operation should fail"));
        } catch (InvalidCustomerCardException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAttachCardToCustomerNegativeId", "Customer card should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAttachCardToCustomerNegativeId", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidCustomerIdException.class)
    public void testAttachCardToCustomerZeroId() throws InvalidCustomerIdException {
        try {
            ezshop.attachCardToCustomer(customerCard1, 0);
            Assert.fail(getErrorMsg("testAttachCardToCustomerZeroId", "The operation should fail"));
        } catch (InvalidCustomerCardException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAttachCardToCustomerZeroId", "Customer card should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAttachCardToCustomerZeroId", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidCustomerIdException.class)
    public void testAttachCardToCustomerNullId() throws InvalidCustomerIdException {
        try {
            ezshop.attachCardToCustomer(customerCard1, null);
            Assert.fail(getErrorMsg("testAttachCardToCustomerNullId", "The operation should fail"));
        } catch (InvalidCustomerCardException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAttachCardToCustomerNullId", "Customer card should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAttachCardToCustomerNullId", "Logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testAttachCardToCustomerNotLogged() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.attachCardToCustomer(customerCard1, 100);
            Assert.fail(getErrorMsg("testAttachCardToCustomerNotLogged", "The operation should fail"));
        } catch (InvalidCustomerIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAttachCardToCustomerNotLogged", "Customer id should not be considered invalid"));
        } catch (InvalidCustomerCardException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAttachCardToCustomerNotLogged", "Customer card should not be considered invalid"));
        }
    }

    @Test
    public void testModifyPointsOnNonExistingCard() {
        try {
            Assert.assertFalse(getErrorMsg("testModifyPointsOnNonExistingCard", "This opearation should not fail"), ezshop.modifyPointsOnCard(customerCard1, point2));
        } catch (InvalidCustomerCardException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testModifyPointsOnNonExistingCard", "Customer card should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testModifyPointsOnNonExistingCard", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidCustomerCardException.class)
    public void testModifyPointsOnInvalidCard() throws InvalidCustomerCardException {
        try {
            ezshop.modifyPointsOnCard(invalidCustomerCard, point2);
            Assert.fail(getErrorMsg("testModifyPointsOnInvalidCard", "The operation should fail"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testModifyPointsOnInvalidCard", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidCustomerCardException.class)
    public void testModifyPointsOnEmptyCard() throws InvalidCustomerCardException {
        try {
            ezshop.modifyPointsOnCard("", point2);
            Assert.fail(getErrorMsg("testModifyPointsOnEmptyCard", "The operation should fail"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testModifyPointsOnEmptyCard", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidCustomerCardException.class)
    public void testModifyPointsOnNullCard() throws InvalidCustomerCardException {
        try {
            ezshop.modifyPointsOnCard(null, point2);
            Assert.fail(getErrorMsg("testModifyPointsOnNullCard", "The operation should fail"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testModifyPointsOnNullCard", "Logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testModifyPointsOnNotLogged() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.modifyPointsOnCard(customerCard1, point2);
            Assert.fail(getErrorMsg("testModifyPointsOnNotLogged", "The operation should fail"));
        } catch (InvalidCustomerCardException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testModifyPointsOnNotLogged", "Customer card should not be considered invalid"));
        }
    }

    @Test
    public void testStartSaleTransaction() {
        try {
            int id = ezshop.startSaleTransaction();
            Assert.assertTrue(getErrorMsg("testStartSaleTransaction", "The id should be greater than 0"), id > 0);
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testStartSaleTransaction", "Logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testStartSaleTransactionNotLogged() throws UnauthorizedException {
        ezshop.logout();
        ezshop.startSaleTransaction();
        Assert.fail(getErrorMsg("testStartSaleTransactionNotLogged", "The operation should fail"));
    }

    @Test
    public void testAddProductToSale() {
        try {
            int prodId = ezshop.createProductType(productDescr1,barCode,pricePerUnit, note1);
            ezshop.updatePosition(prodId,location1);
            ezshop.updateQuantity(prodId,quantity);
            Assert.assertFalse(getErrorMsg("testAddProductToSale", "This operation should fail"), ezshop.addProductToSale(100, barCode,quantity2));
            int transactionId = ezshop.startSaleTransaction();
            Assert.assertTrue(getErrorMsg("testAddProductToSale", "This operation should not fail"), ezshop.addProductToSale(transactionId,barCode,quantity2));
            Assert.assertFalse(getErrorMsg("testAddProductToSale", "This operation should fail"), ezshop.addProductToSale(transactionId,barCode,quantity));
            Assert.assertFalse(getErrorMsg("testAddProductToSale", "This operation should fail"), ezshop.addProductToSale(transactionId,barCode2,quantity2));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSale","Product description should not be considered invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSale","Product price should not be considered invalid"));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSale","Product id should not be considered invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSale","Product amount should not be considered invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSale","Product code should not be considered invalid"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSale","Transaction id should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSale", "The logged user should be authorized"));
        } catch (InvalidLocationException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSale","Product location should not be considered invalid"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testAddProductToSaleNegativeTId() throws InvalidTransactionIdException {
        try {
            ezshop.addProductToSale(-1, barCode, quantity2);
            Assert.fail(getErrorMsg("testAddProductToSaleNegativeTId", "This operation should fail"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSaleNegativeTId","Product amount should not be considered invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSaleNegativeTId","Product code should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSaleNegativeTId", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testAddProductToSaleZeroTId() throws InvalidTransactionIdException {
        try {
            ezshop.addProductToSale(0, barCode, quantity2);
            Assert.fail(getErrorMsg("testAddProductToSaleZeroTId", "This operation should fail"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSaleZeroTId","Product amount should not be considered invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSaleZeroTId","Product code should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSaleZeroTId", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testAddProductToSaleNullTId() throws InvalidTransactionIdException {
        try {
            ezshop.addProductToSale(null, barCode, quantity2);
            Assert.fail(getErrorMsg("testAddProductToSaleNullTId", "This operation should fail"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSaleNullTId","Product amount should not be considered invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSaleNullTId","Product code should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSaleNullTId", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductCodeException.class)
    public void testAddProductToSaleEmptyCode() throws InvalidProductCodeException {
        try {
            ezshop.addProductToSale(100, "", quantity2);
            Assert.fail(getErrorMsg("testAddProductToSaleEmptyCode", "This operation should fail"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSaleEmptyCode","Product amount should not be considered invalid"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSaleEmptyCode","Transaction id should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSaleEmptyCode", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductCodeException.class)
    public void testAddProductToSaleNullCode() throws InvalidProductCodeException {
        try {
            ezshop.addProductToSale(100, null, quantity2);
            Assert.fail(getErrorMsg("testAddProductToSaleNullCode", "This operation should fail"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSaleNullCode","Product amount should not be considered invalid"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSaleNullCode","Transaction id should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSaleNullCode", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductCodeException.class)
    public void testAddProductToSaleInvalidCode() throws InvalidProductCodeException {
        try {
            ezshop.addProductToSale(100, invalidBarCode, quantity2);
            Assert.fail(getErrorMsg("testAddProductToSaleInvalidCode", "This operation should fail"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSaleInvalidCode","Product amount should not be considered invalid"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSaleInvalidCode","Transaction id should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSaleInvalidCode", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidQuantityException.class)
    public void testAddProductToSaleNegativeQuantity() throws InvalidQuantityException {
        try {
            ezshop.addProductToSale(100, barCode, -quantity2);
            Assert.fail(getErrorMsg("testAddProductToSaleNegativeQuantity", "This operation should fail"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSaleNegativeQuantity","Product Code should not be considered invalid"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSaleNegativeQuantity","Transaction id should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSaleNegativeQuantity", "Logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testAddProductToSaleNotLogged() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.addProductToSale(100, barCode, quantity);
            Assert.fail(getErrorMsg("testAddProductToSaleNotLogged", "The operation should fail"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSaleNotLogged","Transaction id should not be considered invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSaleNotLogged","Product code should not be considered invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testAddProductToSaleNotLogged","Product quantity should not be considered invalid"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testDeleteProductFromSaleNegativeTId() throws InvalidTransactionIdException {
        try {
            ezshop.deleteProductFromSale(-1, barCode, quantity2);
            Assert.fail(getErrorMsg("testDeleteProductFromSaleNegativeTId", "This operation should fail"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductFromSaleNegativeTId","Product amount should not be considered invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductFromSaleNegativeTId","Product code should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductFromSaleNegativeTId", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testDeleteProductFromSaleZeroTId() throws InvalidTransactionIdException {
        try {
            ezshop.deleteProductFromSale(0, barCode, quantity2);
            Assert.fail(getErrorMsg("testDeleteProductFromSaleZeroTId", "This operation should fail"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductFromSaleZeroTId","Product amount should not be considered invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductFromSaleZeroTId","Product Code should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductFromSaleZeroTId", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testDeleteProductFromSaleNullTId() throws InvalidTransactionIdException {
        try {
            ezshop.deleteProductFromSale(null, barCode, quantity2);
            Assert.fail(getErrorMsg("testDeleteProductFromSaleNullTId", "This operation should fail"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductFromSaleNullTId","Product amount should not be considered invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductFromSaleNullTId","Product Code should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductFromSaleNullTId", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductCodeException.class)
    public void testDeleteProductFromSaleEmptyCode() throws InvalidProductCodeException {
        try {
            ezshop.deleteProductFromSale(100, "", quantity2);
            Assert.fail(getErrorMsg("testDeleteProductFromSaleEmptyCode", "This operation should fail"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductFromSaleEmptyCode","Product amount should not be considered invalid"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductFromSaleEmptyCode","Transaction id should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductFromSaleEmptyCode", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductCodeException.class)
    public void testDeleteProductFromSaleNullCode() throws InvalidProductCodeException {
        try {
            ezshop.deleteProductFromSale(100, null, quantity2);
            Assert.fail(getErrorMsg("testDeleteProductFromSaleNullCode", "This operation should fail"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductFromSaleNullCode","Product amount should not be considered invalid"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductFromSaleNullCode","Transaction id should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductFromSaleNullCode", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductCodeException.class)
    public void testDeleteProductFromSaleInvalidCode() throws InvalidProductCodeException {
        try {
            ezshop.deleteProductFromSale(100, invalidBarCode, quantity2);
            Assert.fail(getErrorMsg("testDeleteProductFromSaleInvalidCode", "This operation should fail"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductFromSaleInvalidCode","Product amount should not be considered invalid"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductFromSaleInvalidCode","Transaction id should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductFromSaleInvalidCode", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidQuantityException.class)
    public void testDeleteProductFromSaleNegativeQuantity() throws InvalidQuantityException {
        try {
            ezshop.deleteProductFromSale(100, barCode, -quantity2);
            Assert.fail(getErrorMsg("testDeleteProductFromSaleNegativeQuantity", "This operation should fail"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductFromSaleNegativeQuantity","Product code should not be considered invalid"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductFromSaleNegativeQuantity","Transaction id should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductFromSaleNegativeQuantity", "Logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testDeleteProductFromSaleNotLogged() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.deleteProductFromSale(100, barCode, quantity);
            Assert.fail(getErrorMsg("testDeleteProductFromSaleNotLogged", "The operation should fail"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductFromSaleNotLogged","Transaction id should not be considered invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductFromSaleNotLogged","Product code should not be considered invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteProductFromSaleNotLogged","Product quantity should not be considered invalid"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testApplyDiscountRateToProductNegativeTId() throws InvalidTransactionIdException {
        try{
            ezshop.applyDiscountRateToProduct(-1,barCode,discountRate);
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductNegativeTId", "This operation should fail"));
        } catch (InvalidDiscountRateException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductNegativeTId", "Discount rate should not be considered invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductNegativeTId", "Product code should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductNegativeTId", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testApplyDiscountRateToProductZeroTId() throws InvalidTransactionIdException {
        try{
            ezshop.applyDiscountRateToProduct(0,barCode,discountRate);
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductZeroTId", "This operation should fail"));
        } catch (InvalidDiscountRateException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductZeroTId", "Discount rate should not be considered invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductZeroTId", "Product code should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductZeroTId", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testApplyDiscountRateToProductNullTId() throws InvalidTransactionIdException {
        try{
            ezshop.applyDiscountRateToProduct(null,barCode,discountRate);
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductNullTId", "This operation should fail"));
        } catch (InvalidDiscountRateException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductNullTId", "Discount rate should not be considered invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductNullTId", "Product code should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductNullTId", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductCodeException.class)
    public void testApplyDiscountRateToProductNullCode() throws InvalidProductCodeException {
        try{
            ezshop.applyDiscountRateToProduct(100,null,discountRate);
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductNullCode", "This operation should fail"));
        } catch (InvalidDiscountRateException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductNullCode", "Discount rate should not be considered invalid"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductNullCode", "Transaction id should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductNullCode", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductCodeException.class)
    public void testApplyDiscountRateToProductEmptyCode() throws InvalidProductCodeException {
        try{
            ezshop.applyDiscountRateToProduct(100,"",discountRate);
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductEmptyCode", "This operation should fail"));
        } catch (InvalidDiscountRateException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductEmptyCode", "Discount rate should not be considered invalid"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductEmptyCode", "Transaction id should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductEmptyCode", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductCodeException.class)
    public void testApplyDiscountRateToProductInvalidCode() throws InvalidProductCodeException {
        try{
            ezshop.applyDiscountRateToProduct(100,invalidBarCode,discountRate);
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductInvalidCode", "This operation should fail"));
        } catch (InvalidDiscountRateException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductInvalidCode", "Discount rate should not be considered invalid"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductInvalidCode", "Transaction id should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductInvalidCode", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidDiscountRateException.class)
    public void testApplyDiscountRateToProductNegative() throws InvalidDiscountRateException {
        try{
            ezshop.applyDiscountRateToProduct(100,barCode,-0.1);
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductNegative", "This operation should fail"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductNegative", "Product code should not be considered invalid"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductNegative", "Transaction id should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductNegative", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidDiscountRateException.class)
    public void testApplyDiscountRateToProductFree() throws InvalidDiscountRateException {
        try{
            ezshop.applyDiscountRateToProduct(100,barCode,1.0);
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductFree", "This operation should fail"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductFree", "Product code should not be considered invalid"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductFree", "Transaction id should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductFree", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidDiscountRateException.class)
    public void testApplyDiscountRateToProductGreaterThan1() throws InvalidDiscountRateException {
        try{
            ezshop.applyDiscountRateToProduct(100,barCode,invalidDiscountRate);
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductGreaterThan1", "This operation should fail"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductGreaterThan1", "Product code should not be considered invalid"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductGreaterThan1", "Transaction id should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductGreaterThan1", "Logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testApplyDiscountRateToProductNotLogged() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.applyDiscountRateToProduct(100, barCode, discountRate);
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductNotLogged", "The operation should fail"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductNotLogged","Transaction id should not be considered invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductNotLogged","Product code should not be considered invalid"));
        } catch (InvalidDiscountRateException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToProductNotLogged", "Discount rate should not be considered invalid"));
        }
    }

    @Test
    public void testApplyDiscountRateToSale(){
        try{
            int prodId1 = ezshop.createProductType(productDescr1,barCode,pricePerUnit, note1);
            ezshop.updateQuantity(prodId1,quantity);
            int prodId2 = ezshop.createProductType(productDescr2,barCode2,pricePerUnit2, note2);
            ezshop.updateQuantity(prodId2,quantity);
            Assert.assertFalse(getErrorMsg("testApplyDiscountRateToSale", "This operation should fail"), ezshop.applyDiscountRateToSale(100,discountRate));
            int transactionId = ezshop.startSaleTransaction();
            ezshop.addProductToSale(transactionId,barCode,quantity2);
            ezshop.addProductToSale(transactionId,barCode2,2*quantity2);
            Assert.assertTrue(getErrorMsg("testApplyDiscountRateToSale","This operation should not fail"), ezshop.applyDiscountRateToSale(transactionId,discountRate));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToSale", "Product description should not be considered invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToSale", "Product price should not be considered invalid"));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToSale", "Product id should not be considered invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToSale", "Product quantity should not be considered invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToSale", "Product code should not be considered invalid"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToSale", "Transaction id should not be considered invalid"));
        } catch (InvalidDiscountRateException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToSale", "Discount rate should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToSale", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testApplyDiscountRateToSaleNegativeTId() throws InvalidTransactionIdException {
        try{
            ezshop.applyDiscountRateToSale(-1,discountRate);
            Assert.fail(getErrorMsg("testApplyDiscountRateToSaleNegativeTId", "This operation should fail"));
        } catch (InvalidDiscountRateException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToSaleNegativeTId", "Discount rate should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToSaleNegativeTId", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testApplyDiscountRateToSaleNullTId() throws InvalidTransactionIdException {
        try{
            ezshop.applyDiscountRateToSale(null,discountRate);
            Assert.fail(getErrorMsg("testApplyDiscountRateToSaleNullTId", "This operation should fail"));
        } catch (InvalidDiscountRateException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToSaleNullTId", "Discount rate should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToSaleNullTId", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testApplyDiscountRateToSaleZeroTId() throws InvalidTransactionIdException {
        try{
            ezshop.applyDiscountRateToSale(0,discountRate);
            Assert.fail(getErrorMsg("testApplyDiscountRateToSaleZeroTId", "This operation should fail"));
        } catch (InvalidDiscountRateException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToSaleZeroTId", "Discount rate should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToSaleZeroTId", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidDiscountRateException.class)
    public void testApplyDiscountRateToSaleNegative() throws InvalidDiscountRateException {
        try{
            ezshop.applyDiscountRateToSale(100,-0.1);
            Assert.fail(getErrorMsg("testApplyDiscountRateToSaleNegative", "This operation should fail"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToSaleNegative", "Transaction id should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToSaleNegative", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidDiscountRateException.class)
    public void testApplyDiscountRateToSaleFree() throws InvalidDiscountRateException {
        try{
            ezshop.applyDiscountRateToSale(100,1.0);
            Assert.fail(getErrorMsg("testApplyDiscountRateToSaleFree", "This operation should fail"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToSaleFree", "Transaction id should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToSaleFree", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidDiscountRateException.class)
    public void testApplyDiscountRateToSaleGreaterThan1() throws InvalidDiscountRateException {
        try{
            ezshop.applyDiscountRateToSale(100,invalidDiscountRate);
            Assert.fail(getErrorMsg("testApplyDiscountRateToSaleGreaterThan1", "This operation should fail"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToSaleGreaterThan1", "Transaction id should not be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToSaleGreaterThan1", "Logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testApplyDiscountRateToSaleNotLogged() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.applyDiscountRateToSale(100,  discountRate);
            Assert.fail(getErrorMsg("testApplyDiscountRateToSaleNotLogged", "The operation should fail"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToSaleNotLogged","Transaction id should not be considered invalid"));
        } catch (InvalidDiscountRateException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testApplyDiscountRateToSaleNotLogged", "Discount rate should not be considered invalid"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testComputePointsForSaleNegativeTId() throws InvalidTransactionIdException {
        try {
            ezshop.computePointsForSale(-1);
            Assert.fail(getErrorMsg("testComputePointsForSaleNegativeTId", "The operation should have failed"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testComputePointsForSaleNegativeTId", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testComputePointsForSaleNullTId() throws InvalidTransactionIdException {
        try {
            ezshop.computePointsForSale(null);
            Assert.fail(getErrorMsg("testComputePointsForSaleNullTId", "The operation should have failed"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testComputePointsForSaleNullTId", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testComputePointsForSaleZeroTId() throws InvalidTransactionIdException {
        try {
            ezshop.computePointsForSale(0);
            Assert.fail(getErrorMsg("testComputePointsForSaleZeroTId", "The operation should have failed"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testComputePointsForSaleZeroTId", "Logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testComputePointsForSaleNotLogged() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.computePointsForSale(100);
            Assert.fail(getErrorMsg("testComputePointsForSaleNotLogged", "The operation should fail"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testComputePointsForSaleNotLogged", "Transaction id should not be considered invalid"));
        }
    }

    @Test
    public void testDeleteSaleTicket() {
        try{
            int prodId = ezshop.createProductType(productDescr2,barCode2,pricePerUnit2, note2);
            ezshop.updateQuantity(prodId,quantity);
            int id = ezshop.startSaleTransaction();
            Assert.assertTrue(getErrorMsg("testDeleteSaleTicket", "The operation shouldn't have failded"),ezshop.endSaleTransaction(id));
            SaleTransaction tick = ezshop.getSaleTransaction(id);
            Assert.assertTrue(getErrorMsg("testDeleteSaleTicket","The operation should not fail"),ezshop.deleteSaleTransaction(tick.getTicketNumber()));
            Assert.assertNull(getErrorMsg("testDeleteSaleTicket","The ticket should not exist"),ezshop.getSaleTransaction(id));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteSaleTicket", "Transaction id should not be considered invalid"));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteSaleTicket", "Product description should not be considered invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteSaleTicket", "Product code should not be considered invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteSaleTicket", "Product price should not be considered invalid"));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteSaleTicket", "Product id should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteSaleTicket", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testDeleteSaleTicketNegative() throws InvalidTransactionIdException {
        try {
            ezshop.deleteSaleTransaction(-1);
            Assert.fail(getErrorMsg("testDeleteSaleTicketNegative", "Ticket number should be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteSaleTicketNegative", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testDeleteSaleTicketZero() throws InvalidTransactionIdException {
        try {
            ezshop.deleteSaleTransaction(0);
            Assert.fail(getErrorMsg("testDeleteSaleTicketZero", "Ticket number should be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteSaleTicketZero", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testDeleteSaleTicketNull() throws InvalidTransactionIdException {
        try {
            ezshop.deleteSaleTransaction(null);
            Assert.fail(getErrorMsg("testDeleteSaleTicketNull", "Ticket number should be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteSaleTicketNull", "Logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testDeleteSaleTicketNotLogged() throws UnauthorizedException {
        try{
            ezshop.logout();
            ezshop.deleteSaleTransaction(100);
            Assert.fail(getErrorMsg("testDeleteSaleTicketNotLogged", "The operation should fail"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteSaleTicketNotLogged", "Ticket number should not be considered invalid"));
        }
    }

    @Test
    public void testCloseSaleTransaction() {
        try {
            Assert.assertFalse(getErrorMsg("testCloseSaleTransaction", "The operation should have failded"), ezshop.endSaleTransaction(100));
            int prodId = ezshop.createProductType(productDescr2,barCode2,pricePerUnit2, note2);
            ezshop.updateQuantity(prodId,quantity);
            int id = ezshop.startSaleTransaction();
            Assert.assertTrue(getErrorMsg("testCloseSaleTransaction", "The operation shouldn't have failded"),ezshop.endSaleTransaction(id));
            Assert.assertFalse(getErrorMsg("testCloseSaleTransaction","The operation shoud have failed"),ezshop.addProductToSale(id,barCode2,quantity));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCloseSaleTransaction", "Transaction id should not be considered invalid"));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCloseSaleTransaction", "Product description should not be considered invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCloseSaleTransaction", "Product code should not be considered invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCloseSaleTransaction", "Product price should not be considered invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCloseSaleTransaction", "Product quantity should not be considered invalid"));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCloseSaleTransaction", "Product id should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCloseSaleTransaction", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testCloseSaleTransactionNegative() throws InvalidTransactionIdException {
        try {
            ezshop.endSaleTransaction(-1);
            Assert.fail(getErrorMsg("testCloseSaleTransactionNegative", "Transaction id should be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCloseSaleTransactionNegative", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testCloseSaleTransactionZero() throws InvalidTransactionIdException {
        try {
            ezshop.endSaleTransaction(0);
            Assert.fail(getErrorMsg("testCloseSaleTransactionZero", "Transaction id should be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCloseSaleTransactionZero", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testCloseSaleTransactionNull() throws InvalidTransactionIdException {
        try {
            ezshop.endSaleTransaction(-1);
            Assert.fail(getErrorMsg("testCloseSaleTransactionNull", "Transaction id should be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCloseSaleTransactionNull", "Logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testCloseSaleTransactionNotLogged() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.endSaleTransaction(100);
            Assert.fail(getErrorMsg("testCloseSaleTransactionNotLogged", "The operation should fail"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testCloseSaleTransactionNotLogged", "Transaction id should not be considered invalid"));
        }
    }

    @Test
    public void testgetSaleTransaction(){
        try{
            int prodId1 = ezshop.createProductType(productDescr1,barCode,pricePerUnit, note1);
            ezshop.updatePosition(prodId1,location1);
            ezshop.updateQuantity(prodId1,10*quantity);
            int prodId2 = ezshop.createProductType(productDescr2,barCode2,pricePerUnit2, note2);
            ezshop.updatePosition(prodId2,location2);
            ezshop.updateQuantity(prodId2,10*quantity);
            int transactionId = ezshop.startSaleTransaction();
            ezshop.addProductToSale(transactionId,barCode,quantity);
            ezshop.applyDiscountRateToProduct(transactionId,barCode,discountRate);
            ezshop.addProductToSale(transactionId,barCode2,quantity);
            ezshop.applyDiscountRateToSale(transactionId,discountRate);

            Assert.assertNull(getErrorMsg("testgetSaleTransaction","Return value should have been null"),ezshop.getSaleTransaction(transactionId));
            ezshop.endSaleTransaction(transactionId);
            SaleTransaction saleTransaction = ezshop.getSaleTransaction(transactionId);
            Assert.assertNotNull(getErrorMsg("testgetSaleTransaction","Return value should not have been null"), saleTransaction);
            Assert.assertEquals(getErrorMsg("testgetSaleTransaction","Return value is different than expected"),discountRate, saleTransaction.getDiscountRate(),0.0);
            double priceWithoutSaleDiscount = (quantity*pricePerUnit - quantity*pricePerUnit*discountRate) + quantity*pricePerUnit2;//17.50
            double priceWithSaleDiscount = priceWithoutSaleDiscount - (priceWithoutSaleDiscount*discountRate);
            Assert.assertEquals(getErrorMsg("testgetSaleTransaction","Return value is different than expected"),priceWithSaleDiscount, saleTransaction.getPrice(),0.0);
            Assert.assertEquals(getErrorMsg("testgetSaleTransaction","Return value is different than expected"),2, saleTransaction.getEntries().size());
            boolean found1 = false;
            boolean found2 = false;
            double priceTmp = 0;
            for(TicketEntry entry : saleTransaction.getEntries()){
                if(entry.getBarCode().equals(barCode) && !found1){
                    Assert.assertEquals(getErrorMsg("testgetSaleTransaction","Return value is different than expected"),quantity,entry.getAmount());
                    Assert.assertEquals(getErrorMsg("testgetSaleTransaction","Return value is different than expected"),barCode,entry.getBarCode());
                    Assert.assertEquals(getErrorMsg("testgetSaleTransaction","Return value is different than expected"),discountRate,entry.getDiscountRate(),0.0);
                    Assert.assertEquals(getErrorMsg("testgetSaleTransaction","Return value is different than expected"),pricePerUnit,entry.getPricePerUnit(),0.0);
                    Assert.assertEquals(getErrorMsg("testgetSaleTransaction","Return value is different than expected"), productDescr1,entry.getProductDescription());
                    priceTmp += (pricePerUnit*quantity) - (pricePerUnit*quantity*discountRate);
                    found1 = true;
                } else if( entry.getBarCode().equals(barCode2) && !found2 ){
                    Assert.assertEquals(getErrorMsg("testgetSaleTransaction","Return value is different than expected"),quantity,entry.getAmount());
                    Assert.assertEquals(getErrorMsg("testgetSaleTransaction","Return value is different than expected"),barCode2,entry.getBarCode());
                    Assert.assertEquals(getErrorMsg("testgetSaleTransaction","Return value is different than expected"),0.0,entry.getDiscountRate(),0.0);
                    Assert.assertEquals(getErrorMsg("testgetSaleTransaction","Return value is different than expected"),pricePerUnit2,entry.getPricePerUnit(),0.0);
                    Assert.assertEquals(getErrorMsg("testgetSaleTransaction","Return value is different than expected"), productDescr2,entry.getProductDescription());
                    priceTmp += pricePerUnit2*quantity;
                    found2 = true;
                }
            }
            Assert.assertEquals(getErrorMsg("testgetSaleTransaction","Return value is different than expected"),priceWithoutSaleDiscount,priceTmp,0.0);
            Assert.assertTrue(getErrorMsg("testgetSaleTransaction","Ticket has not the expected entries"),found1 && found2);
            int ticketNumber1 = saleTransaction.getTicketNumber();

            transactionId = ezshop.startSaleTransaction();
            ezshop.addProductToSale(transactionId,barCode,quantity);
            ezshop.addProductToSale(transactionId,barCode2,quantity);
            ezshop.endSaleTransaction(transactionId);
            saleTransaction = ezshop.getSaleTransaction(transactionId);
            Assert.assertEquals(getErrorMsg("testgetSaleTransaction","Return value is different than expected"),0.0, saleTransaction.getDiscountRate(),0.0);
            priceWithoutSaleDiscount = quantity*pricePerUnit + quantity*pricePerUnit2;
            Assert.assertEquals(getErrorMsg("testgetSaleTransaction","Return value is different than expected"),priceWithoutSaleDiscount, saleTransaction.getPrice(),0.0);
            Assert.assertEquals(getErrorMsg("testgetSaleTransaction","Return value is different than expected"),2, saleTransaction.getEntries().size());
            found1 = false;
            found2 = false;
            priceTmp = 0;
            for(TicketEntry entry : saleTransaction.getEntries()){
                if(entry.getBarCode().equals(barCode) && !found1){
                    Assert.assertEquals(getErrorMsg("testgetSaleTransaction","Return value is different than expected"),quantity,entry.getAmount());
                    Assert.assertEquals(getErrorMsg("testgetSaleTransaction","Return value is different than expected"),barCode,entry.getBarCode());
                    Assert.assertEquals(getErrorMsg("testgetSaleTransaction","Return value is different than expected"),0.0,entry.getDiscountRate(),0.0);
                    Assert.assertEquals(getErrorMsg("testgetSaleTransaction","Return value is different than expected"),pricePerUnit,entry.getPricePerUnit(),0.0);
                    Assert.assertEquals(getErrorMsg("testgetSaleTransaction","Return value is different than expected"), productDescr1,entry.getProductDescription());
                    priceTmp += pricePerUnit*quantity;
                    found1 = true;
                } else if( entry.getBarCode().equals(barCode2) && !found2 ){
                    Assert.assertEquals(getErrorMsg("testgetSaleTransaction","Return value is different than expected"),quantity,entry.getAmount());
                    Assert.assertEquals(getErrorMsg("testgetSaleTransaction","Return value is different than expected"),barCode2,entry.getBarCode());
                    Assert.assertEquals(getErrorMsg("testgetSaleTransaction","Return value is different than expected"),0.0,entry.getDiscountRate(),0.0);
                    Assert.assertEquals(getErrorMsg("testgetSaleTransaction","Return value is different than expected"),pricePerUnit2,entry.getPricePerUnit(),0.0);
                    Assert.assertEquals(getErrorMsg("testgetSaleTransaction","Return value is different than expected"), productDescr2,entry.getProductDescription());
                    priceTmp += pricePerUnit2*quantity;
                    found2 = true;
                }
            }
            Assert.assertEquals(getErrorMsg("testgetSaleTransaction","Return value is different than expected"),priceWithoutSaleDiscount,priceTmp,0.0);
            Assert.assertTrue(getErrorMsg("testgetSaleTransaction","Ticket has not the expected entries"),found1 && found2);
            Assert.assertNotEquals(getErrorMsg("testgetSaleTransaction","Return value is different than expected"),ticketNumber1, saleTransaction.getTicketNumber().intValue());
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testgetSaleTransaction","Product description should not be considered invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testgetSaleTransaction","Product price should not be considered invalid"));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testgetSaleTransaction","Product id should not be considered invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testgetSaleTransaction","Product quantity should not be considered invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testgetSaleTransaction","Product code should not be considered invalid"));
        } catch (InvalidDiscountRateException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testgetSaleTransaction","Discount rate should not be considered invalid"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testgetSaleTransaction","Transaction id should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testgetSaleTransaction", "The logged user should be authorized"));
        } catch (InvalidLocationException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testgetSaleTransaction","Product location should not be considered invalid"));
        }
    }

    @Test()
    public void testgetSaleTransactionNonExistent(){
        try {
            Assert.assertNull(getErrorMsg("testgetSaleTransactionNonExistent","Return value should be null"),ezshop.getSaleTransaction(100));
        } catch(InvalidTransactionIdException exc){
            exc.printStackTrace();
            Assert.fail(getErrorMsg("testgetSaleTransactionNonExistent","The transaction id should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testgetSaleTransactionNonExistent", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testgetSaleTransactionNegative() throws InvalidTransactionIdException {
        try {
            ezshop.getSaleTransaction(-1);
            Assert.fail(getErrorMsg("testgetSaleTransactionNegative", "The operation should have failed"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testgetSaleTransactionNegative", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testgetSaleTransactionNull() throws InvalidTransactionIdException {
        try {
            ezshop.getSaleTransaction(null);
            Assert.fail(getErrorMsg("testgetSaleTransactionNull", "The operation should have failed"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testgetSaleTransactionNull", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testgetSaleTransactionZero() throws InvalidTransactionIdException {
        try {
            ezshop.getSaleTransaction(0);
            Assert.fail(getErrorMsg("testgetSaleTransactionZero", "The operation should have failed"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testgetSaleTransactionZero", "The logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testgetSaleTransactionNotLogged() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.getSaleTransaction(100);
            Assert.fail(getErrorMsg("testgetSaleTransactionNotLogged", "The operation should fail"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testgetSaleTransactionNotLogged", "Transaction id should not be considered invalid"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testStartReturnTransactionNegative() throws InvalidTransactionIdException {
        try {
            ezshop.startReturnTransaction(-1);
            Assert.fail(getErrorMsg("testStartReturnTransactionNegative", "The operation should have failed"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testStartReturnTransactionNegative", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testStartReturnTransactionNull() throws InvalidTransactionIdException {
        try {
            ezshop.startReturnTransaction(null);
            Assert.fail(getErrorMsg("testStartReturnTransactionNull", "The operation should have failed"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testStartReturnTransactionNull", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testStartReturnTransactionZero() throws InvalidTransactionIdException {
        try {
            ezshop.startReturnTransaction(0);
            Assert.fail(getErrorMsg("testStartReturnTransactionZero", "The operation should have failed"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testStartReturnTransactionZero", "The logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testStartReturnTransactionNotLogged() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.startReturnTransaction(100);
            Assert.fail(getErrorMsg("testStartReturnTransactionNotLogged", "The operation should fail"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testStartReturnTransactionNotLogged", "Ticket number should not be considered invalid"));
        }
    }

    @Test
    public void testReturnProduct(){
        try{
            Assert.assertFalse(getErrorMsg("testReturnProduct","Cannot return product without a transaction"),ezshop.returnProduct(100,barCode,quantity));

            int prodId1 = ezshop.createProductType(productDescr1,barCode,pricePerUnit, note1);
            ezshop.updatePosition(prodId1,location1);
            ezshop.updateQuantity(prodId1,10*quantity);
            int prodId2 = ezshop.createProductType(productDescr2,barCode2,pricePerUnit2, note2);
            ezshop.updatePosition(prodId2,location2);
            ezshop.updateQuantity(prodId2,10*quantity);
            int transactionId = ezshop.startSaleTransaction();
            ezshop.addProductToSale(transactionId,barCode,2*quantity);
            ezshop.endSaleTransaction(transactionId);
            SaleTransaction saleTransaction = ezshop.getSaleTransaction(transactionId);
            ezshop.receiveCashPayment(saleTransaction.getTicketNumber(), saleTransaction.getPrice() );
            int returnId = ezshop.startReturnTransaction(saleTransaction.getTicketNumber());
            Assert.assertTrue(getErrorMsg("testReturnProduct","Return value should have been true"),ezshop.returnProduct(returnId,barCode,quantity));
            Assert.assertFalse(getErrorMsg("testReturnProduct","Return value should have been false"),ezshop.returnProduct(returnId,barCode2,quantity));
            Assert.assertTrue(getErrorMsg("testReturnProduct","Return value should have been true"),ezshop.returnProduct(returnId,barCode,quantity));
            Assert.assertFalse(getErrorMsg("testReturnProduct","Return value should have been false"),ezshop.returnProduct(returnId,barCode2,quantity));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProduct", "Transaction id should not be considered invalid"));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProduct", "Product id should not be considered invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProduct", "Product quantity should not be considered invalid"));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProduct", "Product description should not be considered invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProduct", "Product code should not be considered invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProduct", "Product price should not be considered invalid"));
        } catch (InvalidPaymentException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProduct", "Payment should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProduct", "The logged user should be authorized"));
        } catch (InvalidLocationException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProduct", "Product location should not be considered invalid"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testReturnProductNegativeTId() throws InvalidTransactionIdException {
        try{
            ezshop.returnProduct(-1,barCode,quantity);
            Assert.fail(getErrorMsg("testReturnProductNegativeTId","The operation should have failed"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProductNegativeTId", "Product quantity should not be considered invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProductNegativeTId", "Product code should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProductNegativeTId", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testReturnProductNullTId() throws InvalidTransactionIdException {
        try{
            ezshop.returnProduct(null,barCode,quantity);
            Assert.fail(getErrorMsg("testReturnProductNullTId","The operation should have failed"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProductNullTId", "Product quantity should not be considered invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProductNullTId", "Product code should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProductNullTId", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testReturnProductZeroTId() throws InvalidTransactionIdException {
        try{
            ezshop.returnProduct(0,barCode,quantity);
            Assert.fail(getErrorMsg("testReturnProductNegativeTId","The operation should have failed"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProductNegativeTId", "Product quantity should not be considered invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProductNegativeTId", "Product code should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProductNegativeTId", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductCodeException.class)
    public void testReturnProductEmptyCode() throws InvalidProductCodeException {
        try{
            ezshop.returnProduct(100,"",quantity);
            Assert.fail(getErrorMsg("testReturnProductEmptyCode","The operation should have failed"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProductEmptyCode", "Product quantity should not be considered invalid"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProductEmptyCode", "Transaction id should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProductEmptyCode", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductCodeException.class)
    public void testReturnProductNullCode() throws InvalidProductCodeException {
        try{
            ezshop.returnProduct(100,null,quantity);
            Assert.fail(getErrorMsg("testReturnProductNullCode","The operation should have failed"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProductNullCode", "Product quantity should not be considered invalid"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProductNullCode", "Transaction id should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProductNullCode", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidProductCodeException.class)
    public void testReturnProductInvalidCode() throws InvalidProductCodeException {
        try{
            ezshop.returnProduct(100,invalidBarCode,quantity);
            Assert.fail(getErrorMsg("testReturnProductInvalidCode","The operation should have failed"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProductInvalidCode", "Product quantity should not be considered invalid"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProductInvalidCode", "Transaction id should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProductInvalidCode", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidQuantityException.class)
    public void testReturnProductNegativeQuantity() throws InvalidQuantityException {
        try{
            ezshop.returnProduct(100,barCode,-quantity);
            Assert.fail(getErrorMsg("testReturnProductNegativeQuantity","The operation should have failed"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProductNegativeQuantity", "Product code should not be considered invalid"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProductNegativeQuantity", "Transaction id should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProductNegativeQuantity", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidQuantityException.class)
    public void testReturnProductZeroQuantity() throws InvalidQuantityException {
        try{
            ezshop.returnProduct(100,barCode,0);
            Assert.fail(getErrorMsg("testReturnProductZeroQuantity","The operation should have failed"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProductZeroQuantity", "Product code should not be considered invalid"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProductZeroQuantity", "Transaction id should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProductZeroQuantity", "The logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testReturnProductNotLogged() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.returnProduct(100,barCode,quantity);
            Assert.fail(getErrorMsg("testReturnProductNotLogged", "The operation should fail"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProductNotLogged", "Transaction id should not be considered invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProductNotLogged", "Product code should not be considered invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProductNotLogged", "Product quantity should not be considered invalid"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testEndReturnTransactionNegative() throws InvalidTransactionIdException {
        try {
            ezshop.endReturnTransaction(-1, true);
            Assert.fail(getErrorMsg("testEndReturnTransactionNegative", "The operation should have failed"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testEndReturnTransactionNegative", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testEndReturnTransactionZero() throws InvalidTransactionIdException {
        try {
            ezshop.endReturnTransaction(0, true);
            Assert.fail(getErrorMsg("testEndReturnTransactionZero", "The operation should have failed"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testEndReturnTransactionZero", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testEndReturnTransactionNull() throws InvalidTransactionIdException {
        try {
            ezshop.endReturnTransaction(null, true);
            Assert.fail(getErrorMsg("testEndReturnTransactionNull", "The operation should have failed"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testEndReturnTransactionNull", "The logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testEndReturnTransactionNotLogged() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.endReturnTransaction(100,true);
            Assert.fail(getErrorMsg("testReturnProductNotLogged", "The operation should fail"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnProductNotLogged", "Transaction id should not be considered invalid"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testDeleteReturnTransactionNegative() throws InvalidTransactionIdException {
        try {
            ezshop.deleteReturnTransaction(-1);
            Assert.fail(getErrorMsg("testDeleteReturnTransactionNegative", "Transaction id should be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteReturnTransactionNegative", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testDeleteReturnTransactionZero() throws InvalidTransactionIdException {
        try {
            ezshop.deleteReturnTransaction(0);
            Assert.fail(getErrorMsg("testDeleteReturnTransactionZero", "Transaction id should be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteReturnTransactionZero", "Logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testDeleteReturnTransactionNull() throws InvalidTransactionIdException {
        try {
            ezshop.deleteReturnTransaction(null);
            Assert.fail(getErrorMsg("testDeleteReturnTransactionNull", "Transaction id should be considered invalid"));
        } catch (UnauthorizedException e){
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteReturnTransactionNull", "Logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testDeleteReturnTransactionNotLogged() throws UnauthorizedException {
        try{
            ezshop.logout();
            ezshop.deleteReturnTransaction(100);
            Assert.fail(getErrorMsg("testDeleteSaleTicketNotLogged", "The operation should fail"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testDeleteSaleTicketNotLogged", "Transaction id should not be considered invalid"));
        }
    }

    @Test
    public void testReceiveCashPayment(){
        try{
            Assert.assertFalse(getErrorMsg("testReceiveCashPayment","Return value should have been false"),ezshop.receiveCashPayment(100,cash) >= 0);
            int prodId1 = ezshop.createProductType(productDescr1,barCode,pricePerUnit, note1);
            ezshop.updatePosition(prodId1,location1);
            ezshop.updateQuantity(prodId1,10*quantity);
            int transactionId = ezshop.startSaleTransaction();
            ezshop.addProductToSale(transactionId,barCode,2*quantity);
            ezshop.endSaleTransaction(transactionId);
            SaleTransaction saleTransaction = ezshop.getSaleTransaction(transactionId);
            Assert.assertEquals(getErrorMsg("testReceiveCashPayment","There should be no change"),0,ezshop.receiveCashPayment(saleTransaction.getTicketNumber(),cash*2),0.0);

            transactionId = ezshop.startSaleTransaction();
            ezshop.addProductToSale(transactionId,barCode,quantity);
            ezshop.endSaleTransaction(transactionId);
            saleTransaction = ezshop.getSaleTransaction(transactionId);

            Assert.assertEquals(getErrorMsg("testReceiveCashPayment","There should be some change"),cash,ezshop.receiveCashPayment(saleTransaction.getTicketNumber(),cash*2),0.0);
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCashPayment", "Transaction id should not be considered invalid"));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCashPayment", "Product id should not be considered invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCashPayment", "Product quantity should not be considered invalid"));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCashPayment", "Product description should not be considered invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCashPayment", "Product code should not be considered invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCashPayment", "Product price should not be considered invalid"));
        } catch (InvalidPaymentException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCashPayment", "Payment should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCashPayment", "The logged user should be authorized"));
        } catch (InvalidLocationException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCashPayment", "Product location should not be considered invalid"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testReceiveCashPaymentNegativeTicket() throws InvalidTransactionIdException {
        try{
            ezshop.receiveCashPayment(-1,cash);
            Assert.fail(getErrorMsg("testReceiveCashPaymentNegativeTicket", "The operation should have failed"));
        } catch (InvalidPaymentException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCashPaymentNegativeTicket", "Payment should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCashPaymentNegativeTicket", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testReceiveCashPaymentNullTicket() throws InvalidTransactionIdException {
        try{
            ezshop.receiveCashPayment(null,cash);
            Assert.fail(getErrorMsg("testReceiveCashPaymentNullTicket", "The operation should have failed"));
        } catch (InvalidPaymentException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCashPaymentNullTicket", "Payment should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCashPaymentNullTicket", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testReceiveCashPaymentZeroTicket() throws InvalidTransactionIdException {
        try{
            ezshop.receiveCashPayment(-1,cash);
            Assert.fail(getErrorMsg("testReceiveCashPaymentZeroTicket", "The operation should have failed"));
        } catch (InvalidPaymentException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCashPaymentZeroTicket", "Payment should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCashPaymentZeroTicket", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidPaymentException.class)
    public void testReceiveNegativeCashPayment() throws InvalidPaymentException {
        try{
            ezshop.receiveCashPayment(100,-0.1);
            Assert.fail(getErrorMsg("testReceiveNegativeCashPayment", "The operation should have failed"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveNegativeCashPayment", "Payment should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveNegativeCashPayment", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidPaymentException.class)
    public void testReceiveZeroCashPayment() throws InvalidPaymentException {
        try{
            ezshop.receiveCashPayment(100,0);
            Assert.fail(getErrorMsg("testReceiveZeroCashPayment", "The operation should have failed"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveZeroCashPayment", "Payment should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveZeroCashPayment", "The logged user should be authorized"));
        }
    }

    @Test
    public void testReceiveCashPaymentLessThanNeeded(){
        try{
            int prodId1 = ezshop.createProductType(productDescr1,barCode,pricePerUnit, note1);
            ezshop.updatePosition(prodId1,location1);
            ezshop.updateQuantity(prodId1,10*quantity);
            int transactionId = ezshop.startSaleTransaction();
            ezshop.addProductToSale(transactionId,barCode,2*quantity);
            ezshop.endSaleTransaction(transactionId);
            SaleTransaction saleTransaction = ezshop.getSaleTransaction(transactionId);
            Assert.assertEquals(getErrorMsg("testReceiveCashPaymentLessThanNeeded", "The operation should have failed"),-1,ezshop.receiveCashPayment(saleTransaction.getTicketNumber(),cash),0.0);
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCashPaymentLessThanNeeded", "Transaction id should not be considered invalid"));
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCashPaymentLessThanNeeded", "Product id should not be considered invalid"));
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCashPaymentLessThanNeeded", "Product quantity should not be considered invalid"));
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCashPaymentLessThanNeeded", "Product description should not be considered invalid"));
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCashPaymentLessThanNeeded", "Product code should not be considered invalid"));
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCashPaymentLessThanNeeded", "Product price should not be considered invalid"));
        } catch (InvalidPaymentException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCashPaymentLessThanNeeded", "Payment should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCashPaymentLessThanNeeded", "The logged user should be authorized"));
        } catch (InvalidLocationException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCashPaymentLessThanNeeded", "Product location should not be considered invalid"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testReceiveCashPaymentNotLogged() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.receiveCashPayment(100,cash);
            Assert.fail(getErrorMsg("testReceiveCashPaymentNotLogged", "The operation should fail"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCashPaymentNotLogged", "Ticket Number should not be considered invalid"));
        } catch (InvalidPaymentException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCashPaymentNotLogged", "Payment should not be considered invalid"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testReceiveCreditCardPaymentNegativeTicket() throws InvalidTransactionIdException {
        try{
            ezshop.receiveCreditCardPayment(-1,creditCard150);
            Assert.fail(getErrorMsg("testReceiveCreditCardPaymentNegativeTicket", "The operation should have failed"));
        } catch (InvalidCreditCardException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCreditCardPaymentNegativeTicket", "Credit card should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCreditCardPaymentNegativeTicket", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testReceiveCreditCardPaymentZeroTicket() throws InvalidTransactionIdException {
        try{
            ezshop.receiveCreditCardPayment(0,creditCard150);
            Assert.fail(getErrorMsg("testReceiveCreditCardPaymentZeroTicket", "The operation should have failed"));
        } catch (InvalidCreditCardException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCreditCardPaymentZeroTicket", "Credit card should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCreditCardPaymentZeroTicket", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testReceiveCreditCardPaymentNullTicket() throws InvalidTransactionIdException {
        try{
            ezshop.receiveCreditCardPayment(null,creditCard150);
            Assert.fail(getErrorMsg("testReceiveCreditCardPaymentNullTicket", "The operation should have failed"));
        } catch (InvalidCreditCardException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCreditCardPaymentNullTicket", "Credit card should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCreditCardPaymentNullTicket", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidCreditCardException.class)
    public void testReceiveEmptyCreditCardPayment() throws InvalidCreditCardException {
        try{
            ezshop.receiveCreditCardPayment(100, "");
            Assert.fail(getErrorMsg("testReceiveCreditCardPaymentNullTicket", "The operation should have failed"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCreditCardPaymentNullTicket", "Credit card should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCreditCardPaymentNullTicket", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidCreditCardException.class)
    public void testReceiveNullCreditCardPayment() throws InvalidCreditCardException {
        try{
            ezshop.receiveCreditCardPayment(100, null);
            Assert.fail(getErrorMsg("testReceiveNullCreditCardPayment", "The operation should have failed"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveNullCreditCardPayment", "Credit card should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveNullCreditCardPayment", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidCreditCardException.class)
    public void testReceiveInvalidCreditCardPayment() throws InvalidCreditCardException {
        try{
            ezshop.receiveCreditCardPayment(100, invalidCreditCard);
            Assert.fail(getErrorMsg("testReceiveInvalidCreditCardPayment", "The operation should have failed"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveInvalidCreditCardPayment", "Credit card should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveInvalidCreditCardPayment", "The logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testReceiveCreditCardPaymentNotLogged() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.receiveCreditCardPayment(100,creditCard150);
            Assert.fail(getErrorMsg("testReceiveCreditCardPaymentNotLogged", "The operation should fail"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCreditCardPaymentNotLogged", "Ticket Number should not be considered invalid"));
        } catch (InvalidCreditCardException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReceiveCreditCardPaymentNotLogged", "Credit card should not be considered invalid"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testReturnCashPaymentNegative() throws InvalidTransactionIdException {
        try {
            ezshop.returnCashPayment(-1);
            Assert.fail(getErrorMsg("testReturnCashPaymentNegative", "The operation should have failed"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnCashPaymentNegative", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testReturnCashPaymentNull() throws InvalidTransactionIdException {
        try {
            ezshop.returnCashPayment(null);
            Assert.fail(getErrorMsg("testReturnCashPaymentNull", "The operation should have failed"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnCashPaymentNull", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testReturnCashPaymentZero() throws InvalidTransactionIdException {
        try {
            ezshop.returnCashPayment(0);
            Assert.fail(getErrorMsg("testReturnCashPaymentZero", "The operation should have failed"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnCashPaymentZero", "The logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testReturnCashPaymentNotLogged() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.returnCashPayment(100);
            Assert.fail(getErrorMsg("testReturnCashPaymentNotLogged", "The operation should fail"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnCashPaymentNotLogged", "Transaction id should not be considered invalid"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testReturnCreditCardPaymentNegative() throws InvalidTransactionIdException {
        try{
            ezshop.returnCreditCardPayment(-1,creditCard150);
            Assert.fail(getErrorMsg("testReturnCreditCardPaymentNegative", "The operation should have failed"));
        } catch (InvalidCreditCardException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnCreditCardPaymentNegative", "Credit card should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnCreditCardPaymentNegative", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testReturnCreditCardPaymentNull() throws InvalidTransactionIdException {
        try{
            ezshop.returnCreditCardPayment(null,creditCard150);
            Assert.fail(getErrorMsg("testReturnCreditCardPaymentNull", "The operation should have failed"));
        } catch (InvalidCreditCardException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnCreditCardPaymentNull", "Credit card should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnCreditCardPaymentNull", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidTransactionIdException.class)
    public void testReturnCreditCardPaymentZero() throws InvalidTransactionIdException {
        try{
            ezshop.returnCreditCardPayment(0,creditCard150);
            Assert.fail(getErrorMsg("testReturnCreditCardPaymentZero", "The operation should have failed"));
        } catch (InvalidCreditCardException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnCreditCardPaymentZero", "Credit card should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnCreditCardPaymentZero", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidCreditCardException.class)
    public void testReturnEmptyCreditCardPayment() throws InvalidCreditCardException {
        try{
            ezshop.returnCreditCardPayment(10,"");
            Assert.fail(getErrorMsg("testReturnEmptyCreditCardPayment", "The operation should have failed"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnEmptyCreditCardPayment", "Transaction id should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnEmptyCreditCardPayment", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidCreditCardException.class)
    public void testReturnNullCreditCardPayment() throws InvalidCreditCardException {
        try{
            ezshop.returnCreditCardPayment(10,null);
            Assert.fail(getErrorMsg("testReturnNullCreditCardPayment", "The operation should have failed"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnNullCreditCardPayment", "Transaction id should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnNullCreditCardPayment", "The logged user should be authorized"));
        }
    }

    @Test(expected = InvalidCreditCardException.class)
    public void testReturnInvalidCreditCardPayment() throws InvalidCreditCardException {
        try{
            ezshop.returnCreditCardPayment(10,invalidCreditCard);
            Assert.fail(getErrorMsg("testReturnInvalidCreditCardPayment", "The operation should have failed"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnInvalidCreditCardPayment", "Transaction id should not be considered invalid"));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnInvalidCreditCardPayment", "The logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testReturnCreditCardPaymentNotLogged() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.returnCreditCardPayment(100,creditCard150);
            Assert.fail(getErrorMsg("testReturnCreditCardPaymentNotLogged", "The operation should fail"));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnCreditCardPaymentNotLogged", "Transaction id should not be considered invalid"));
        } catch (InvalidCreditCardException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testReturnCreditCardPaymentNotLogged", "Credit card should not be considered invalid"));
        }
    }

    @Test
    public void testRecordBalanceUpdate() {
        try {
            Assert.assertTrue(getErrorMsg("testRecordBalanceUpdate", "The operation should not fail"), ezshop.recordBalanceUpdate(quantity * pricePerUnit));
            double balance = ezshop.computeBalance();
            Assert.assertEquals(getErrorMsg("testRecordBalanceUpdate", "Balance does not match the expected value"), quantity * pricePerUnit, balance, 0.0);
            Assert.assertTrue(getErrorMsg("testRecordBalanceUpdate", "The operation should not fail"), ezshop.recordBalanceUpdate(-quantity * pricePerUnit));
            balance = ezshop.computeBalance();
            Assert.assertEquals(getErrorMsg("testRecordBalanceUpdate", "Balance does not match the expected value"), 0, balance, 0.0);
            Assert.assertFalse(getErrorMsg("testRecordBalanceUpdate", "The operation should fail"), ezshop.recordBalanceUpdate(-quantity * pricePerUnit));
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordBalanceUpdate", "The logged user should be authorized"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testRecordBalanceUpdateNotLogged() throws UnauthorizedException {
        ezshop.logout();
        ezshop.recordBalanceUpdate(pricePerUnit);
        Assert.fail(getErrorMsg("testRecordBalanceUpdateNotLogged", "The operation should fail"));
    }

    @Test(expected = UnauthorizedException.class)
    public void testRecordBalanceUpdateNotAuthorized() throws UnauthorizedException {
        try {
            ezshop.logout();
            ezshop.createUser(username1,userPwd,cashier);
            ezshop.login(username1,userPwd);
            ezshop.recordBalanceUpdate(pricePerUnit);
            Assert.fail(getErrorMsg("testRecordBalanceUpdateNotAuthorized", "The operation should fail"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordBalanceUpdateNotAuthorized", "The role should not be considered as invalid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordBalanceUpdateNotAuthorized", "The password should not be considered as invalid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordBalanceUpdateNotAuthorized", "The username should not be considered as invalid"));
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetBalanceOperationsNotLogged() throws UnauthorizedException {
        ezshop.logout();
        LocalDate to = LocalDate.now();
        LocalDate from = LocalDate.now();
        ezshop.getCreditsAndDebits(from,to);
        Assert.fail(getErrorMsg("testGetBalanceOperationsNotLogged", "The operation should fail"));
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetBalanceOperationsNotAuthorized() throws UnauthorizedException {
        try {
            LocalDate to = LocalDate.now();
            LocalDate from = LocalDate.now();
            ezshop.logout();
            ezshop.createUser(username1,userPwd,cashier);
            ezshop.login(username1,userPwd);
            ezshop.getCreditsAndDebits(from,to);
            Assert.fail(getErrorMsg("testRecordBalanceUpdateNotAuthorized", "The operation should fail"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordBalanceUpdateNotAuthorized", "The role should not be considered as invalid"));
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordBalanceUpdateNotAuthorized", "The password should not be considered as invalid"));
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
            Assert.fail(getErrorMsg("testRecordBalanceUpdateNotAuthorized", "The username should not be considered as invalid"));
        }
    }
}
