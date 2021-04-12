import Stars from "./Stars";

function Option(props) {

  const restaurant = props.restaurant;

  return (
    <div className="restaurant">
      <div className="title-text">{restaurant.name}</div>
      <div className="rest-info">
        <div><Stars number={restaurant.star} /> &#183; {restaurant.numReviews} reviews</div>
        <div>{restaurant.priceRange} &#183; {restaurant.distance}</div>
        <div>{restaurant.foodType}</div>
      </div>
    </div>
  );
}

export default Option;