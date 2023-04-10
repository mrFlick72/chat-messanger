import React, {useState} from 'react';
import ReactDOM from 'react-dom';
import {Button, Grid, Paper, TextField, Typography} from "@mui/material";

const Application = () => {

    const [userName, setUserName] = useState<string>("")
    const createRoom = () => {
        console.log("click")
        console.log(`userName: ${userName}`)
        fetch("/room", {
            method: "POST",
            credentials: "same-origin",
            body: userName
        })
    }

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>): void => {
        setUserName(e.target.value);
    }

    return <Paper elevation={3}>
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <Typography variant={"h3"}>Create a new Room with</Typography>
            </Grid>
            <Grid item xs={4}>
                <TextField label="user to invite" fullWidth value={userName}
                           onChange={handleChange}/>
            </Grid>
            <Grid item xs={8}>
                <Button type="button" variant="outlined" onClick={createRoom}>Invite</Button>
            </Grid>
        </Grid>
    </Paper>

};

ReactDOM.render(<Application/>, document.getElementById('app'));