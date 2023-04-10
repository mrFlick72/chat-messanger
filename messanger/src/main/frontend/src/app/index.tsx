import React, {useState} from 'react';
import ReactDOM from 'react-dom';
import {Button, Grid, Paper, TextField, Typography} from "@mui/material";

const Application = () => {

    const [userName, setUserName] = useState("")
    const createRoom = () => {
        console.log("click")
    }

    return <Paper elevation={3}>
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <Typography variant={"h3"}>Create a new Room with</Typography>
            </Grid>
            <Grid item xs={4}>
                <TextField label="user to invite" fullWidth/>
            </Grid>
            <Grid item xs={8}>
                <Button type="submit" variant="outlined" onClick={() => createRoom}>Invite</Button>
            </Grid>
        </Grid>
    </Paper>

}

ReactDOM.render(<Application/>, document.getElementById('app'));