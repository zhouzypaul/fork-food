import Stars from "./Stars";

function Option(props) {

  const restaurant = props.restaurant;

  const price = (p) => {
    let ret = "";
    for (let i = 0; i < p; i++) {
      ret += "$";
    }
    return ret;
  }
  console.log(restaurant.star)

  return (
    <div className="restaurant">
      <div className="title-text">{restaurant.name}</div>
      <div className="rest-info">
        <div><Stars number={Math.ceil(restaurant.star)}/> from {restaurant.numReviews} reviews</div>
        <div>{price(restaurant.priceRange)} &#183; {restaurant.distance.toFixed(1)} mi</div>
        <div>{restaurant.foodType}</div>
      </div>
    </div>
  );
}

export default Option;