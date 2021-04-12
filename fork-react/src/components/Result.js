function Result(props) {

    const restaurant = props.location.resultProps.result;

    return (
        <div>

            You're resulting now.
            <div>
                This is your restaurant selection: {restaurant}. Yeah its just an id but you could look it up in the database i guess.
            </div>
        </div>
    );
}

export default Result;