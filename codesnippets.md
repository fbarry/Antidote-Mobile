# Code Snippets


## Get a List of all parseObjects with a certain class ID
ParseQuery<ParseObject> query = ParseQuery.getQuery("Card");
List<ParseObject> thelist = query.find();

StringBuilder disp = new StringBuilder();
for (ParseObject object : thelist) {
    disp.append(object.getObjectId()).append(" ");
}
extView2.setText(disp.toString());

## Get a specific row of a database object by ObjectID (In this case, ImFD78s4so of Card)
ParseQuery<ParseObject> query = ParseQuery.getQuery("Card");
query.getInBackground("ImFD78s4so", new GetCallback<ParseObject>() {
    @Override
    public void done(ParseObject object, ParseException e) {
        if (e == null) {
            textView2.append(" "+object.toString());

            int cardnum = object.getInt("cardNum");
            String type = object.getString("type");
            display += cardnum + " ";
            display += type;
        } else {
            textView2.setText(e.getMessage());
            e.printStackTrace();
        }
    }
});