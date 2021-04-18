import Stars from "./Stars";

/**
 * Renders restaurant option for swiping.
 */
function Option(props) {
  const restaurant = props.restaurant;

  // redirect to home if restaurant is undefined
  if (!props.restaurant) {
    window.location.href = "/home";
  }

  // get price in terms of dollar signs
  const price = (p) => {
    let ret = "";
    for (let i = 0; i < p; i++) {
      ret += "$";
    }
    return ret;
  }

  return (
    <div className="restaurant">
      <div className="title-text">{restaurant.name}</div>
      <div className="rest-info">
        <div><Stars number={restaurant.star} /> from {restaurant.numReviews} reviews</div>
        <div>{price(restaurant.priceRange)} &#8226; {restaurant.distance.toFixed(1)} mi</div>
        <div>{restaurant.foodType}</div>
      </div>
    </div>
  );
}

export default Option;